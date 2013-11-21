package cn.edu.uestc.acmicpc.db.dto.impl.userSerialKey;

import java.sql.Timestamp;
import java.util.Map;

import cn.edu.uestc.acmicpc.db.dto.base.BaseBuilder;
import cn.edu.uestc.acmicpc.db.dto.base.BaseDTO;
import cn.edu.uestc.acmicpc.db.entity.UserSerialKey;
import cn.edu.uestc.acmicpc.util.annotation.Fields;

/** Data transfer object for user serial key **/
@Fields({ "userSerialKeyId", "time", "serialKey", "userId" })
public class UserSerialKeyDTO implements BaseDTO<UserSerialKey> {

  public UserSerialKeyDTO() {
  }

  private UserSerialKeyDTO(Integer userSerialKeyId, Timestamp time, String serialKey, Integer userId) {
    this.userSerialKeyId = userSerialKeyId;
    this.time = time;
    this.serialKey = serialKey;
    this.userId = userId;
  }

  private Integer userSerialKeyId;
  private Timestamp time;
  private String serialKey;
  private Integer userId;

  public Integer getUserSerialKeyId() {
    return userSerialKeyId;
  }

  public void setUserSerialKeyId(Integer userSerialKeyId) {
    this.userSerialKeyId = userSerialKeyId;
  }

  public Timestamp getTime() {
    return time;
  }

  public void setTime(Timestamp time) {
    this.time = time;
  }

  public String getSerialKey() {
    return serialKey;
  }

  public void setSerialKey(String serialKey) {
    this.serialKey = serialKey;
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

  public static class Builder implements BaseBuilder<UserSerialKeyDTO> {

    private Builder() {
    }

    @Override
    public UserSerialKeyDTO build() {
      return new UserSerialKeyDTO(userSerialKeyId, time, serialKey, userId);
    }

    @Override
    public UserSerialKeyDTO build(Map<String, Object> properties) {
      userSerialKeyId = (Integer) properties.get("userSerialKeyId");
      time = (Timestamp) properties.get("time");
      serialKey = (String) properties.get("serialKey");
      userId = (Integer) properties.get("userId");
      return build();

    }

    private Integer userSerialKeyId;
    private Timestamp time;
    private String serialKey;
    private Integer userId;

    public Integer getUserSerialKeyId() {
      return userSerialKeyId;
    }

    public Builder setUserSerialKeyId(Integer userSerialKeyId) {
      this.userSerialKeyId = userSerialKeyId;
      return this;
    }

    public Timestamp getTime() {
      return time;
    }

    public Builder setTime(Timestamp time) {
      this.time = time;
      return this;
    }

    public String getSerialKey() {
      return serialKey;
    }

    public Builder setSerialKey(String serialKey) {
      this.serialKey = serialKey;
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
