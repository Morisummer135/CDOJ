package cn.edu.uestc.acmicpc.web.oj.controller.problem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.edu.uestc.acmicpc.db.condition.impl.ProblemCondition;
import cn.edu.uestc.acmicpc.db.condition.impl.StatusCondition;
import cn.edu.uestc.acmicpc.db.dto.impl.problem.ProblemDTO;
import cn.edu.uestc.acmicpc.db.dto.impl.problem.ProblemDataEditDTO;
import cn.edu.uestc.acmicpc.db.dto.impl.problem.ProblemDataShowDTO;
import cn.edu.uestc.acmicpc.db.dto.impl.problem.ProblemEditDTO;
import cn.edu.uestc.acmicpc.db.dto.impl.problem.ProblemEditorShowDTO;
import cn.edu.uestc.acmicpc.db.dto.impl.problem.ProblemListDTO;
import cn.edu.uestc.acmicpc.db.dto.impl.problem.ProblemShowDTO;
import cn.edu.uestc.acmicpc.db.dto.impl.user.UserDTO;
import cn.edu.uestc.acmicpc.service.iface.FileService;
import cn.edu.uestc.acmicpc.service.iface.LanguageService;
import cn.edu.uestc.acmicpc.service.iface.PictureService;
import cn.edu.uestc.acmicpc.service.iface.ProblemService;
import cn.edu.uestc.acmicpc.service.iface.StatusService;
import cn.edu.uestc.acmicpc.util.annotation.LoginPermit;
import cn.edu.uestc.acmicpc.util.exception.AppException;
import cn.edu.uestc.acmicpc.util.exception.FieldException;
import cn.edu.uestc.acmicpc.util.helper.StringUtil;
import cn.edu.uestc.acmicpc.util.settings.Global;
import cn.edu.uestc.acmicpc.util.settings.Global.OnlineJudgeResultType;
import cn.edu.uestc.acmicpc.web.dto.FileUploadDTO;
import cn.edu.uestc.acmicpc.web.dto.PageInfo;
import cn.edu.uestc.acmicpc.web.oj.controller.base.BaseController;

@Controller
@RequestMapping("/problem")
public class ProblemController extends BaseController {

  private ProblemService problemService;
  private StatusService statusService;
  private LanguageService languageService;
  private PictureService pictureService;
  private FileService fileService;

  @Autowired
  public void setFileService(FileService fileService) {
    this.fileService = fileService;
  }

  @Autowired
  public void setPictureService(PictureService pictureService) {
    this.pictureService = pictureService;
  }

  @Autowired
  public void setProblemService(ProblemService problemService) {
    this.problemService = problemService;
  }

  @Autowired
  public void setStatusService(StatusService statusService) {
    this.statusService = statusService;
  }

  @Autowired
  public void setLanguageService(LanguageService languageService) {
    this.languageService = languageService;
  }

  /**
   * Show a problem
   *
   * @param problemId
   * @param model
   * @return String
   */
  @RequestMapping("show/{problemId}")
  @LoginPermit(NeedLogin = false)
  public String show(@PathVariable("problemId") Integer problemId,
      ModelMap model) {
    try {
      ProblemShowDTO problemShowDTO = problemService
          .getProblemShowDTO(problemId);
      if (problemShowDTO == null) {
        throw new AppException("No such problem.");
      }

      Map<Global.OnlineJudgeResultType, Long> problemStatistic = new TreeMap<>();
      for (Global.OnlineJudgeResultType type: Global.OnlineJudgeResultType.values()) {
        if (type == OnlineJudgeResultType.OJ_WAIT) {
          continue;
        }
        StatusCondition statusCondition = new StatusCondition();
        statusCondition.result = type;
        statusCondition.problemId = problemId;
        statusCondition.contestId = -1;
        statusCondition.isVisible = true;
        problemStatistic.put(type, statusService.count(statusCondition));
      }

      model.put("problemStatistic", problemStatistic);
      model.put("targetProblem", problemShowDTO);
      model.put("brToken", "\n");
      model.put("languageList", languageService.getLanguageList());
    } catch (AppException e) {
      return "error/404";
    } catch (Exception e) {
      e.printStackTrace();
      return "error/404";
    }
    return "problem/problemShow";
  }

  /**
   * Show problem list
   *
   * @return String
   */
  @RequestMapping("list")
  @LoginPermit(NeedLogin = false)
  public String list() {
    return "problem/problemList";
  }

  /**
   * Search problem
   *
   * @param session
   * @param problemCondition
   * @return json
   */
  @RequestMapping("search")
  @LoginPermit(NeedLogin = false)
  public @ResponseBody
  Map<String, Object> search(HttpSession session,
      @RequestBody ProblemCondition problemCondition) {
    Map<String, Object> json = new HashMap<>();
    try {
      UserDTO currentUser = (UserDTO) session.getAttribute("currentUser");
      if (currentUser != null
          && currentUser.getType() == Global.AuthenticationType.ADMIN.ordinal()) {
        // We can put some special condition here
      } else {
        problemCondition.isVisible = true;
      }
      Long count = problemService.count(problemCondition);
      PageInfo pageInfo = buildPageInfo(count, problemCondition.currentPage,
          Global.RECORD_PER_PAGE, "", null);

      List<ProblemListDTO> problemListDTOList = problemService
          .getProblemListDTOList(
              problemCondition, pageInfo);

      Map<Integer, Global.AuthorStatusType> problemStatus = GetProblemStatus(currentUser);

      for (ProblemListDTO problemListDTO : problemListDTOList) {
        if (problemStatus.get(problemListDTO.getProblemId()) == Global.AuthorStatusType.PASS) {
          problemListDTO.setStatus(1);
        }
        else if (problemStatus.containsKey(problemListDTO.getProblemId())) {
          problemListDTO.setStatus(2);
        }
      }

      if (pageInfo.getTotalPages() != 1) {
        json.put("pageInfo", pageInfo.getHtmlString());
      }
      json.put("result", "success");
      json.put("list", problemListDTOList);
    } catch (AppException e) {
      json.put("result", "error");
      json.put("error_msg", e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      json.put("result", "error");
      json.put("error_msg", "Unknown exception occurred.");
    }
    return json;
  }

  /**
   * Modify special field of problem
   *
   * @param targetId
   *          problem id
   * @param field
   *          field want to modified
   * @param value
   *          value
   * @return JSON
   */
  @RequestMapping("operator/{id}/{field}/{value}")
  @LoginPermit(Global.AuthenticationType.ADMIN)
  public @ResponseBody
  Map<String, Object> operator(@PathVariable("id") String targetId,
      @PathVariable("field") String field,
      @PathVariable("value") String value) {
    Map<String, Object> json = new HashMap<>();
    try {
      problemService.operator(field, targetId, value);
      json.put("result", "success");
    } catch (Exception e) {
      e.printStackTrace();
      json.put("result", "error");
      json.put("error_msg", "Unknown exception occurred.");
    }
    return json;
  }

  /**
   * Open problem editor
   *
   * @param sProblemId
   *          target problem id or "new"
   * @param model
   *          model
   * @return editor view
   */
  @RequestMapping("editor/{problemId}")
  @LoginPermit(Global.AuthenticationType.ADMIN)
  public String editor(@PathVariable("problemId") String sProblemId,
      ModelMap model) {
    try {
      if (sProblemId.compareTo("new") == 0) {
        model.put("action", "new");
      } else {
        Integer problemId;
        try {
          problemId = Integer.parseInt(sProblemId);
        } catch (NumberFormatException e) {
          throw new AppException("Parse problem id error.");
        }
        ProblemEditorShowDTO targetProblem = problemService
            .getProblemEditorShowDTO(problemId);
        if (targetProblem == null)
          throw new AppException("No such problem.");
        model.put("action", "edit");
        model.put("targetProblem", targetProblem);
      }
    } catch (AppException e) {
      model.put("message", e.getMessage());
      return "error/error";
    }
    return "/problem/problemEditor";
  }

  /**
   * Edit problem
   *
   * @param problemEditDTO
   *          uploaded information
   * @param validateResult
   *          validate result
   * @return
   */
  @RequestMapping("edit")
  @LoginPermit(Global.AuthenticationType.ADMIN)
  public @ResponseBody
  Map<String, Object> edit(@RequestBody @Valid ProblemEditDTO problemEditDTO,
      BindingResult validateResult) {
    Map<String, Object> json = new HashMap<>();
    if (validateResult.hasErrors()) {
      json.put("result", "field_error");
      json.put("field", validateResult.getFieldErrors());
    } else {
      try {
        if (StringUtil.trimAllSpace(problemEditDTO.getTitle()).equals(""))
          throw new FieldException("title", "Please enter a validate title.");
        ProblemDTO problemDTO;
        if (problemEditDTO.getAction().compareTo("new") == 0) {
          Integer problemId = problemService.createNewProblem();
          problemDTO = problemService.getProblemDTOByProblemId(problemId);
          if (problemDTO == null
              || !problemDTO.getProblemId().equals(problemId))
            throw new AppException("Error while creating problem.");
          // Move pictures
          String oldDirectory = "/images/problem/new/";
          String newDirectory = "/images/problem/" + problemId + "/";
          problemEditDTO.setDescription(pictureService.modifyPictureLocation(
              problemEditDTO.getDescription(), oldDirectory, newDirectory));
          problemEditDTO.setInput(pictureService.modifyPictureLocation(
              problemEditDTO.getInput(), oldDirectory, newDirectory));
          problemEditDTO.setOutput(pictureService.modifyPictureLocation(
              problemEditDTO.getOutput(), oldDirectory, newDirectory));
          problemEditDTO.setHint(pictureService.modifyPictureLocation(
              problemEditDTO.getHint(), oldDirectory, newDirectory));
        } else {
          problemDTO = problemService.getProblemDTOByProblemId(problemEditDTO
              .getProblemId());
          if (problemDTO == null)
            throw new AppException("No such problem.");
        }

        problemDTO.setTitle(problemEditDTO.getTitle());
        problemDTO.setDescription(problemEditDTO.getDescription());
        problemDTO.setInput(problemEditDTO.getInput());
        problemDTO.setOutput(problemEditDTO.getOutput());
        problemDTO.setSampleInput(problemEditDTO.getSampleInput());
        problemDTO.setSampleOutput(problemEditDTO.getSampleOutput());
        problemDTO.setHint(problemEditDTO.getHint());
        problemDTO.setSource(problemEditDTO.getSource());

        problemService.updateProblem(problemDTO);
        json.put("result", "success");
        json.put("problemId", problemDTO.getProblemId());
      } catch (FieldException e) {
        putFieldErrorsIntoBindingResult(e, validateResult);
        json.put("result", "field_error");
        json.put("field", validateResult.getFieldErrors());
      } catch (AppException e) {
        json.put("result", "error");
        json.put("error_msg", e.getMessage());
      }
    }
    return json;
  }

  @RequestMapping("dataEditor/{problemId}")
  @LoginPermit(Global.AuthenticationType.ADMIN)
  public String dataEditor(@PathVariable("problemId") Integer problemId,
      ModelMap model) {
    try {
      ProblemDataShowDTO targetProblem = problemService
          .getProblemDataShowDTO(problemId);
      if (targetProblem == null)
        throw new AppException("No such problem.");
      model.put("targetProblem", targetProblem);
    } catch (AppException e) {
      model.put("message", e.getMessage());
      return "error/error";
    }
    return "problem/problemDataEditor";
  }

  @RequestMapping(value = "uploadProblemDataFile/{problemId}",
      method = RequestMethod.POST)
  @LoginPermit(Global.AuthenticationType.ADMIN)
  public @ResponseBody
  Map<String, Object> uploadProblemDataFile(
      @PathVariable("problemId") Integer problemId,
      @RequestParam(value = "uploadFile", required = true) MultipartFile[] files) {
    Map<String, Object> json = new HashMap<>();
    try {
      if (problemId == null)
        throw new AppException("Error, target problem id is null!");
      ProblemDTO problemDTO = problemService
          .getProblemDTOByProblemId(problemId);
      if (problemDTO == null)
        throw new AppException("Error, target problem doesn't exist!");

      Integer dataCount = fileService.uploadProblemDataFile(
          FileUploadDTO.builder()
              .setFiles(Arrays.asList(files))
              .build(),
          problemId);

      json.put("total", dataCount);
      json.put("success", "true");
    } catch (AppException e) {
      e.printStackTrace();
      json.put("error", e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      json.put("error", "Unknown exception occurred.");
    }
    return json;
  }

  @RequestMapping("updateProblemData")
  @LoginPermit(Global.AuthenticationType.ADMIN)
  public @ResponseBody
  Map<String, Object> updateProblemData(
      @RequestBody @Valid ProblemDataEditDTO problemDataEditDTO,
      BindingResult validateResult) {
    Map<String, Object> json = new HashMap<>();
    if (validateResult.hasErrors()) {
      json.put("result", "field_error");
      json.put("field", validateResult.getFieldErrors());
    } else {
      try {
        if (problemDataEditDTO.getTimeLimit() < 0 ||
            problemDataEditDTO.getTimeLimit() > 60000)
          throw new FieldException("timeLimit",
              "Time limit should between 0 and 60000");
        if (problemDataEditDTO.getJavaTimeLimit() < 0 ||
            problemDataEditDTO.getJavaTimeLimit() > 60000)
          throw new FieldException("javaTimeLimit",
              "Time limit should between 0 and 60000");
        if (problemDataEditDTO.getMemoryLimit() < 0 ||
            problemDataEditDTO.getMemoryLimit() > 262144)
          throw new FieldException("memoryLimit",
              "Memory limit should between 0 and 262144");
        if (problemDataEditDTO.getJavaMemoryLimit() < 0 ||
            problemDataEditDTO.getJavaMemoryLimit() > 262144)
          throw new FieldException("javaMemoryLimit",
              "Memory limit should between 0 and 262144");
        if (problemDataEditDTO.getOutputLimit() < 0 ||
            problemDataEditDTO.getOutputLimit() > 262144)
          throw new FieldException("outputLimit",
              "Output limit should between 0 and 262144");
        ProblemDTO problemDTO = problemService
            .getProblemDTOByProblemId(problemDataEditDTO.getProblemId());
        if (problemDTO == null)
          throw new AppException("No such problem.");

        problemDTO.setTimeLimit(problemDataEditDTO.getTimeLimit());
        problemDTO.setJavaTimeLimit(problemDataEditDTO.getJavaTimeLimit());
        problemDTO.setMemoryLimit(problemDataEditDTO.getMemoryLimit());
        problemDTO.setJavaMemoryLimit(problemDataEditDTO.getJavaMemoryLimit());
        problemDTO.setOutputLimit(problemDataEditDTO.getOutputLimit());
        problemDTO.setIsSpj(problemDataEditDTO.getIsSpj());

        Integer dataCount = fileService.moveProblemDataFile(problemDTO
            .getProblemId());
        if (dataCount != 0)
          problemDTO.setDataCount(dataCount);

        problemService.updateProblem(problemDTO);
        json.put("result", "success");
      } catch (FieldException e) {
        putFieldErrorsIntoBindingResult(e, validateResult);
        json.put("result", "field_error");
        json.put("field", validateResult.getFieldErrors());
      } catch (AppException e) {
        json.put("result", "error");
        json.put("error_msg", e.getMessage());
      }
    }
    return json;
  }

  private Map<Integer, Global.AuthorStatusType> GetProblemStatus(
      UserDTO currentUser) {
    Map<Integer, Global.AuthorStatusType> problemStatus = new HashMap<>();
    try {
      if (currentUser != null) {
        List<Integer> triedProblems = statusService.
            findAllUserTriedProblemIds(currentUser.getUserId());
        for (Integer result : triedProblems) {
          problemStatus.put(result, Global.AuthorStatusType.FAIL);
        }
        List<Integer> acceptedProblems = statusService.
            findAllUserAcceptedProblemIds(currentUser.getUserId());
        for (Integer result : acceptedProblems) {
          problemStatus.put(result, Global.AuthorStatusType.PASS);
        }
      }
    } catch (AppException ignored) {
    }
    return problemStatus;
  }
}
