package cn.edu.uestc.acmicpc.db.dto.impl.contestUser;

import cn.edu.uestc.acmicpc.db.dto.base.BaseBuilder;
import cn.edu.uestc.acmicpc.db.dto.base.BaseDTO;
import cn.edu.uestc.acmicpc.db.entity.ContestUser;
import cn.edu.uestc.acmicpc.util.annotation.Fields;

import java.util.Map;

/**
 * DTO used in contest user entity.
 */

@Fields({"contestUserId", "contestId", "userId", "status"})
public class ContestUserDTO implements BaseDTO<ContestUser> {
  private Integer contestUserId;
  private Integer contestId;
  private Integer userId;
  private Byte status;

  public ContestUserDTO() {}

  public ContestUserDTO(Integer contestUserId, Integer contestId, Integer userId,
                        Byte status) {
    this.contestUserId = contestUserId;
    this.contestId = contestId;
    this.userId = userId;
    this.status = status;
  }

  public Integer getContestUserId() {
    return contestUserId;
  }

  public void setContestUserId(Integer contestUserId) {
    this.contestUserId = contestUserId;
  }

  public Integer getContestId() {
    return contestId;
  }

  public void setContestId(Integer contestId) {
    this.contestId = contestId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public Byte getStatus() {
    return status;
  }

  public void setStatus(Byte status) {
    this.status = status;
  }

  public static Builder Builder() {
    return new Builder();
  }

  public static class Builder implements BaseBuilder<ContestUserDTO> {

    private Integer contestUserId;
    private Integer contestId;
    private Integer userId;
    private Byte status;

    @Override
    public ContestUserDTO build() {
      return new ContestUserDTO(contestUserId, contestId, userId, status);
    }

    @Override
    public ContestUserDTO build(Map<String, Object> properties) {
      contestUserId = (Integer) properties.get("contestUserId");
      contestId = (Integer) properties.get("contestId");
      userId = (Integer) properties.get("userId");
      status = (Byte) properties.get("status");
      return build();
    }

    public Builder setContestUserId(Integer contestUserId) {
      this.contestUserId = contestUserId;
      return this;
    }

    public Builder setContestId(Integer contestId) {
      this.contestId = contestId;
      return this;
    }

    public Builder setUserId(Integer userId) {
      this.userId = userId;
      return this;
    }

    public Builder setStatus(Byte status) {
      this.status = status;
      return this;
    }
  }
}
