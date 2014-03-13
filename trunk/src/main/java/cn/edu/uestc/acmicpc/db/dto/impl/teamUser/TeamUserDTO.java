package cn.edu.uestc.acmicpc.db.dto.impl.teamUser;

import cn.edu.uestc.acmicpc.db.dto.base.BaseBuilder;
import cn.edu.uestc.acmicpc.db.dto.base.BaseDTO;
import cn.edu.uestc.acmicpc.db.entity.TeamUser;
import cn.edu.uestc.acmicpc.util.annotation.Fields;

import java.util.Map;

@Fields({ "teamUserId", "teamId", "userId" })
public class TeamUserDTO implements BaseDTO<TeamUser> {

  public TeamUserDTO() {
  }

  private TeamUserDTO(Integer teamUserId, Integer teamId, Integer userId) {
    this.teamUserId = teamUserId;
    this.teamId = teamId;
    this.userId = userId;
  }

  private Integer teamUserId;
  private Integer teamId;
  private Integer userId;

  public Integer getTeamUserId() {
    return teamUserId;
  }

  public void setTeamUserId(Integer teamUserId) {
    this.teamUserId = teamUserId;
  }

  public Integer getTeamId() {
    return teamId;
  }

  public void setTeamId(Integer teamId) {
    this.teamId = teamId;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder implements BaseBuilder<TeamUserDTO> {

    private Builder() {
    }

    @Override
    public TeamUserDTO build() {
      return new TeamUserDTO(teamUserId, teamId, userId);
    }

    @Override
    public TeamUserDTO build(Map<String, Object> properties) {
      teamUserId = (Integer) properties.get("teamUserId");
      teamId = (Integer) properties.get("teamId");
      userId = (Integer) properties.get("userId");
      return build();

    }

    private Integer teamUserId;
    private Integer teamId;
    private Integer userId;

    public Integer getTeamUserId() {
      return teamUserId;
    }

    public Builder setTeamUserId(Integer teamUserId) {
      this.teamUserId = teamUserId;
      return this;
    }

    public Integer getTeamId() {
      return teamId;
    }

    public Builder setTeamId(Integer teamId) {
      this.teamId = teamId;
      return this;
    }

    public Integer getUserId() {
      return userId;
    }

    public Builder setUserId(Integer userId) {
      this.userId = userId;
      return this;
    }
  }
}
