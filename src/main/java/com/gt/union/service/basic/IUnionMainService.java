package com.gt.union.service.basic;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.gt.union.entity.basic.UnionMain;
import com.gt.union.entity.basic.vo.UnionMainCreateInfoVO;
import com.gt.union.entity.basic.vo.UnionMainInfoVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 联盟主表 服务类
 * </p>
 *
 * @author linweicong
 * @since 2017-07-21
 */
public interface IUnionMainService extends IService<UnionMain> {
    /**
     * 首页查询我的联盟信息
     * @param busId
     * @return
     * @throws Exception
     */
    public Map<String, Object> indexByBusId(Integer busId) throws Exception;

    /**
     * 选择联盟时查询我的联盟信息
     * @param id
     * @param busId
	 * @return
     * @throws Exception
     */
    public Map<String, Object> indexById(Integer id, Integer busId) throws Exception;

    /**
     * 查询商家创建及加入的所有联盟列表
     * @param busId
     * @return
     * @throws Exception
     */
    public List<UnionMain> listMyUnion(Integer busId) throws Exception;

    /**
     * 查询所有联盟
     * @return
     * @throws Exception
     */
    public Page list(Page page) throws Exception;

    /**
     * 更新联盟信息，要求盟主权限
     * @param id
     * @param busId
     * @param vo
     * @throws Exception
     */
    public void updateById(Integer id, Integer busId, UnionMainInfoVO vo) throws Exception;

    /**
     * 创建联盟
     * @param busId
     * @throws Exception
     */
    public Map<String, Object> instance(Integer busId) throws Exception;

    /**
     * 保存创建联盟的信息
     * @param busId
     * @param unionMainCreateInfoVO
     * @throws Exception
     */
    public void save(Integer busId, UnionMainCreateInfoVO unionMainCreateInfoVO) throws Exception;

    /**
     * 通过busId获取他创建的联盟
     * @param busId
     * @return
     * @throws Exception
     */
    public UnionMain getByBusId(Integer busId) throws Exception;

    /**
     * 根据id获取对象
     * @param id
     * @return
     * @throws Exception
     */
    public UnionMain getById(Integer id) throws Exception;

    /**
     * 获取商家加入的联盟和该联盟列表的交集
     * @param list  联盟列表
     * @param busId 商家id
     * @return
     */
	List<UnionMain> getSameUnionBus(List<UnionMain> list, Integer busId);

    /**
     * 查询我的有效联盟
     * @param busId
     * @return
     */
    List<UnionMain> listMyValidUnion(Integer busId);


    /**
     * 创建联盟支付的二维码信息
     * @param busId  用户信息
     * @param infoItemKey   支付类型
     * @return
     */
	Map<String,Object> createUnionQRCode(Integer busId, String infoItemKey) throws Exception;

    /**
     * 创建联盟成功回调
     * @param recordEncrypt recirdId密文
     * @param only  时间戳
     */
    void payCreateUnionSuccess(String recordEncrypt, String only) throws Exception;
}
