package com.trade.service.impl;

import com.trade.service.TradeService;
import com.trade.vo.OrderVo;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author georgy
 * @Date 2020-01-14 下午 2:40
 * @DESC TODO
 */
@Service
public class TradeServiceImpl implements TradeService {

    @Override
    public void open(OrderVo orderVo) {
        tradeOrders.add(orderVo);
    }

    @Override
    public void close(OrderVo orderVo, BigDecimal closePrice) {
        tradeOrders.remove(orderVo);
        BigDecimal bp = BigDecimal.ZERO;
        // 计算交易损益(BP)
        if(orderVo.getDirection() == 1){ // 多头头寸平仓计算 BP
            bp = closePrice.subtract(orderVo.getPrice()).multiply(orderVo.getVolume());

        }else if(orderVo.getDirection() == 0){ // 空头头寸平仓计算 BP
            bp = orderVo.getPrice().subtract(closePrice).multiply(orderVo.getVolume());

        }else{
            throw new RuntimeException("数据错误: 交易订单没有方向");
        }
        this.calTotalCapital(bp);
    }

    /**
     * 获取交易池中的订单
     * @param tsCode
     * @return
     */
    @Override
    public OrderVo getOrderVo(String tsCode){
        if(!CollectionUtils.isEmpty(tradeOrders)){
            for (OrderVo orderVo : tradeOrders) {
                if(orderVo.getTsCode().equals(tsCode)){
                    return orderVo;
                }
            }
        }
        return null;
    }

    /**
     * 获取总资金
     * @return
     */
    @Override
    public BigDecimal getTotalCapital(){
        return TradeService.assetVo.getTotalCapital();
    }

    /**
     * 核算总资金
     * @return
     */
    @Override
    public void calTotalCapital(BigDecimal bp){
        TradeService.assetVo.setTotalCapital(TradeService.assetVo.getTotalCapital().add(bp));
    }

    /**
     * 获取风险系数
     * @return
     */
    @Override
    public BigDecimal getRiskParameter(){
        return TradeService.assetVo.getRiskParameter();
    }

    /**
     * 获取交易池
     * @return
     */
    @Override
    public List<OrderVo> getTradeOrders(){
        return TradeService.tradeOrders;
    }


}
