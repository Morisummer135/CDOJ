package cn.edu.uestc.acmicpc.db.dto.impl.contest;

import java.sql.Timestamp;
import java.util.Map;

import cn.edu.uestc.acmicpc.db.dto.base.BaseBuilder;
import cn.edu.uestc.acmicpc.db.dto.base.BaseDTO;
import cn.edu.uestc.acmicpc.db.entity.Contest;
import cn.edu.uestc.acmicpc.util.annotation.Fields;
import cn.edu.uestc.acmicpc.util.settings.Global;

/**
 * DTO for contest entity.
 * <br/>
 * <code>@Fields({ "contestId", "title", "description", "time", "length", "type" })</code>
 */
@Fields({ "contestId", "title", "description", "time", "length", "type" })
public class ContestShowDTO implements BaseDTO<Contest> {

  private Integer contestId;
  private String title;
  private String description;
  private String status;
  private Timestamp startTime;
  private Timestamp endTime;
  private Timestamp currentTime;
  private Long timeLeft;
  private Integer length;
  private Byte type;
  private String typeName;

  public ContestShowDTO() {
  }

  public ContestShowDTO(Integer contestId, String title,
                        String description,
                        String status, Timestamp startTime, Timestamp endTime,
                        Timestamp currentTime, Long timeLeft, Integer length, Byte type,
                        String typeName) {
    this.contestId = contestId;
    this.title = title;
    this.description = description;
    this.status = status;
    this.startTime = startTime;
    this.endTime = endTime;
    this.currentTime = currentTime;
    this.timeLeft = timeLeft;
    this.length = length;
    this.setType(type);
    this.setTypeName(typeName);
  }

  public Integer getContestId() {
    return contestId;
  }

  public void setContestId(Integer contestId) {
    this.contestId = contestId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getTimeLeft() {
    return timeLeft;
  }

  public void setTimeLeft(Long timeLeft) {
    this.timeLeft = timeLeft;
  }

  public Timestamp getStartTime() {
    return startTime;
  }

  public void setStartTime(Timestamp startTime) {
    this.startTime = startTime;
  }

  public Timestamp getEndTime() {
    return endTime;
  }

  public void setEndTime(Timestamp endTime) {
    this.endTime = endTime;
  }

  public Timestamp getCurrentTime() {
    return currentTime;
  }

  public void setCurrentTime(Timestamp currentTime) {
    this.currentTime = currentTime;
  }

  public Byte getType() {
    return type;
  }

  public void setType(Byte type) {
    this.type = type;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder implements BaseBuilder<ContestShowDTO> {

    private Integer contestId;
    private String title;
    private String description;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp currentTime;
    private Long timeLeft;
    private Integer length;
    private String status;
    private Byte type;
    private String typeName;

    @Override
    public ContestShowDTO build() {
      return new ContestShowDTO(contestId, title, description,
          status, startTime, endTime, currentTime, timeLeft, length, type,
          typeName);
    }

    @Override
    public ContestShowDTO build(Map<String, Object> properties) {
      contestId = (Integer) properties.get("contestId");
      title = (String) properties.get("title");
      description = (String) properties.get("description");
      length = (Integer) properties.get("length") * 1000;
      startTime = (Timestamp) properties.get("time");
      type = (Byte) properties.get("type");
      typeName = Global.ContestType.values()[type].getDescription();

      endTime = new Timestamp(startTime.getTime() + length);
      currentTime = new Timestamp(System.currentTimeMillis());
      timeLeft = Math.max(endTime.getTime() - currentTime.getTime(), 0L);
      if (timeLeft > length) {
        status = "Pending";
      } else if (timeLeft > 0) {
        status = "Running";
      } else {
        status = "Ended";
      }
      return build();
    }

    public Builder setContestId(Integer contestId) {
      this.contestId = contestId;
      return this;
    }

    public Builder setTitle(String title) {
      this.title = title;
      return this;
    }

    public Builder setDescription(String description) {
      this.description = description;
      return this;
    }

    public Builder setTimeLeft(Long timeLeft) {
      this.timeLeft = timeLeft;
      return this;
    }

    public Builder setLength(Integer length) {
      this.length = length;
      return this;
    }

    public Builder setStatus(String status) {
      this.status = status;
      return this;
    }

    public Timestamp getStartTime() {
      return startTime;
    }

    public Builder setStartTime(Timestamp startTime) {
      this.startTime = startTime;
      return this;
    }

    public Timestamp getEndTime() {
      return endTime;
    }

    public Builder setEndTime(Timestamp endTime) {
      this.endTime = endTime;
      return this;
    }

    public Timestamp getCurrentTime() {
      return currentTime;
    }

    public Builder setCurrentTime(Timestamp currentTime) {
      this.currentTime = currentTime;
      return this;
    }

    public Byte getType() {
      return type;
    }

    public Builder setType(Byte type) {
      this.type = type;
      return this;
    }

    public String getTypeName() {
      return typeName;
    }

    public Builder setTypeName(String typeName) {
      this.typeName = typeName;
      return this;
    }
  }
}