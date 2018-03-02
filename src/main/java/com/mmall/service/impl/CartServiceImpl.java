package com.mmall.service.impl;

import com.google.common.base.Splitter;
import com.mmall.common.Const;
import com.mmall.common.ResponceCode;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CartMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Cart;
import com.mmall.pojo.Product;
import com.mmall.service.ICartService;
import com.mmall.util.BigDecimalUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.CartProductVo;
import com.mmall.vo.CartVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service("iCartServicei")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){
        if(productId==null || count==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.ILLEGAL_ARGUMENT.getCode(),ResponceCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart==null){
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);
            cartMapper.insertSelective(cartItem);
        }else{
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createSuccessDataResponse(cartVo);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        if(productId==null || count==null){
            return ServerResponse.createErrorcodeMessageResponse(ResponceCode.ILLEGAL_ARGUMENT.getCode(),ResponceCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if(cart !=null){
            cart.setQuantity(count);
        }else {
            return ServerResponse.createErrorMessageResponse("该产品并未加入购物车");
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        CartVo cartVo =getCartVoLimit(userId);
        return ServerResponse.createSuccessDataResponse(cartVo);
    }

    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        List<String> productList = Splitter.on(",").splitToList(productIds);
        if(productList.size()==0){
           return ServerResponse.createErrorcodeMessageResponse(ResponceCode.ILLEGAL_ARGUMENT.getCode(),ResponceCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createSuccessDataResponse(cartVo);
    }

    @Override
    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createSuccessDataResponse(cartVo);
    }

    @Override
    public ServerResponse<CartVo> selectAllOrUnSelectAll(Integer userId,Integer productId,Integer checked){
        cartMapper.checkedOrUncheckedProduct(userId,checked,productId);
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createSuccessDataResponse(cartVo);
    }

    @Override
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        if(userId==null){
            return ServerResponse.createSuccessDataResponse(0);
        }
        return ServerResponse.createSuccessDataResponse(cartMapper.selectCartProductCount(userId));
    }


    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        List<Cart> cartList = cartMapper.selectCartsByUserId(userId);
        List<CartProductVo> cartProductVoList = new ArrayList<>();

        BigDecimal cartTotalPrice = new BigDecimal("0");

        if(!CollectionUtils.isEmpty(cartList)){
            for (Cart cartItem:
                 cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                Product product = productMapper.selectByPrimaryKey(cartProductVo.getProductId());
                if(product!=null){
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());

                    int buyLimitCount = 0;
                    if(product.getStock() >= cartItem.getQuantity()){
                        buyLimitCount = cartItem.getQuantity();
                        cartProductVo.setLimitQuatity(Const.Cart.LIMIT_NUM_SUCCESS);
                    }else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuatity(Const.Cart.LIMIT_NUM_FAIL);

                        Cart cartForQuantity = new Cart();
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                    }

                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                    cartProductVo.setChecked(cartItem.getChecked());
                }

                if(cartItem.getChecked() == Const.Cart.CHECKED){
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }

        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId){
        if(userId==null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId)==0?true:false;
    }
}
