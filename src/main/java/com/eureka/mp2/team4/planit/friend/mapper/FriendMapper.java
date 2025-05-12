package com.eureka.mp2.team4.planit.friend.mapper;

import com.eureka.mp2.team4.planit.friend.dto.request.FriendAskDto;
import com.eureka.mp2.team4.planit.friend.dto.FriendDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendMapper {

    void insert(FriendAskDto friendAskDto);

    List<FriendDto> findAllByUserId(String userId);

    List<FriendDto> findPendingByReceiverId(String userId);

    List<FriendDto> findAskedByRequesterId(String userId);

    void updateStatus(@Param("friendId") String friendId, @Param("status") String status);

    FriendDto findById(String friendId);
    void autoCancelOppositePending(@Param("oppositeRequesterId") String oppositeRequesterId,
                                   @Param("oppositeReceiverId") String oppositeReceiverId);

    void delete(String friendId);

}
