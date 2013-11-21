package cn.edu.uestc.acmicpc.db.dao.impl;

import org.springframework.stereotype.Repository;

import cn.edu.uestc.acmicpc.db.dao.base.DAO;
import cn.edu.uestc.acmicpc.db.dao.iface.IUserSerialKeyDAO;
import cn.edu.uestc.acmicpc.db.entity.UserSerialKey;

/**
 * DAO implementation for {@link UserSerialKey}.
 */
@Repository
public class UserSerialKeyDAO extends DAO<UserSerialKey, Integer> implements IUserSerialKeyDAO {

  @Override
  protected Class<Integer> getPKClass() {
    return Integer.class;
  }

  @Override
  protected Class<UserSerialKey> getReferenceClass() {
    return UserSerialKey.class;
  }
}
