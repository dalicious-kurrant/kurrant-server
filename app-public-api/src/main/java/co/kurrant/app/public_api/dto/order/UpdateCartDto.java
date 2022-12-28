package co.kurrant.app.public_api.dto.order;

import lombok.Getter;

import java.util.*;

@Getter
public class UpdateCartDto {
    private List<UpdateCart> updateCartList;
}