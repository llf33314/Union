package com.gt.union.service.common;

import com.gt.union.entity.basic.UnionMember;
import com.gt.union.entity.common.BusUser;

/**
 * 商家联盟权限统一管理类
 * Created by Administrator on 2017/7/25 0025.
 */
public interface IUnionRootService {
    /**
     * 根据用户id获取用户对象
     * @param busId
     * @return
     * @throws Exception
     */
    public BusUser getBusUserByBusId(Integer busId) throws Exception;

    /**
     * 根据用户id判断该用户是否有效
     * @param busId
     * @return
     * @throws Exception
     */
	public boolean checkBusUserValid(Integer busId) throws Exception;

    /**
     * 根据用户判断该用户是否有效
     * @param busUser
     * @return
     * @throws Exception
     */
	public boolean checkBusUserValid(BusUser busUser) throws Exception;

    /**
     * 根据用户id判断该用户是否拥有盟主权限
     * @param busId
     * @return
     * @throws Exception
     */
    public boolean hasUnionOwnerAuthority(Integer busId) throws Exception;

    /**
     * 根据用户id判断该用户是否创建过联盟
     * @param busId
     * @return
     * @throws Exception
     */
    public boolean hasCreatedUnion(Integer busId) throws Exception;

    /**
     * 根据联盟id判断该联盟是否有效
     * @param unionId
     * @return
     * @throws Exception
     */
    public boolean checkUnionMainValid(Integer unionId) throws Exception;

    /**
     * 根据用户id判断该用户是否是某联盟的盟主
     * @param busId
     * @return
     * @throws Exception
     */
    public boolean isUnionOwner(Integer busId) throws Exception;

    /**
     * 判断该用户是否是该联盟的盟主
     * @param unionId
     * @param busId
     * @return
     * @throws Exception
     */
    public boolean isUnionOwner(Integer unionId, Integer busId) throws Exception;

    /**
     * 判断该用户是否具有该联盟的盟员权限，未加盟、已退盟以及退盟过渡期不具有
     * @param unionId
     * @param busId
     * @return
     * @throws Exception
     */
    public boolean hasUnionMemberAuthority(Integer unionId, Integer busId) throws Exception;

    /**
     * 判断该用户是否具有该联盟的盟员权限，未加盟、已退盟以及退盟过渡期不具有。当unionMember为空时，返回false
     * @param unionMember
     * @return
     * @throws Exception
     */
    public boolean hasUnionMemberAuthority(UnionMember unionMember) throws Exception;


}
