package cn.edu.uestc.acmicpc.oj.service.impl;

import java.util.List;

import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import cn.edu.uestc.acmicpc.db.condition.impl.ProblemCondition;
import cn.edu.uestc.acmicpc.db.dao.iface.IProblemDAO;
import cn.edu.uestc.acmicpc.oj.service.iface.ProblemService;
import cn.edu.uestc.acmicpc.service.impl.AbstractService;
import cn.edu.uestc.acmicpc.util.exception.AppException;

/**
 * Implementation for {@link ProblemService}.
 */
@Service
@Primary
public class ProblemServiceImpl extends AbstractService implements ProblemService {

  private final IProblemDAO problemDAO;

  @Autowired
  public ProblemServiceImpl(IProblemDAO problemDAO) {
    this.problemDAO = problemDAO;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<Integer> getAllVisibleProblemIds() throws AppException {
    ProblemCondition problemCondition = new ProblemCondition();
    // TODO set this is problem condition.
    problemCondition.isVisible = true;
    return (List<Integer>) problemDAO.findAll(problemCondition.getCondition().addProjection(
        Projections.id()));
  }

  @Override
  public IProblemDAO getDAO() {
    return problemDAO;
  }
}
