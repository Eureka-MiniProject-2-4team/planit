package com.eureka.mp2.team4.planit.friend.mapper;

import com.eureka.mp2.team4.planit.friend.dto.request.FriendAskDto;
import com.eureka.mp2.team4.planit.friend.dto.response.FriendResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendMapper {

    void insert(FriendAskDto friendAskDto);

    List<FriendResponseDto> findAllByUserId(String userId);

    List<FriendResponseDto> findPendingByReceiverId(String userId);

    List<FriendResponseDto> findAskedByRequesterId(String userId);

    void updateStatus(@Param("friendId") String friendId, @Param("status") String status);

    FriendResponseDto findById(String friendId);
    void autoCancelOppositePending(@Param("oppositeRequesterId") String oppositeRequesterId,
                                   @Param("oppositeReceiverId") String oppositeReceiverId);

    void delete(String friendId);

}
