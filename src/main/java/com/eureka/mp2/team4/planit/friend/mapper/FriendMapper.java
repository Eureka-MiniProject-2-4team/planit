package com.eureka.mp2.team4.planit.friend.mapper;

import com.eureka.mp2.team4.planit.friend.dto.request.FriendAskDto;
import com.eureka.mp2.team4.planit.friend.dto.FriendDto;
import com.eureka.mp2.team4.planit.friend.dto.response.FriendMakeDto;
import com.eureka.mp2.team4.planit.friend.dto.response.UserSearchDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendMapper {

    void insert(FriendMakeDto friendMakeDto);

    List<FriendDto> findAllByUserId(String userId);

    List<FriendDto> findPendingByReceiverId(String userId);

    List<FriendDto> findAskedByRequesterId(String userId);

    void updateStatus(@Param("friendId") String friendId, @Param("status") String status);

    void delete(String friendId);

    FriendDto findById(String friendId);

    FriendDto findByBothUserId(String userId, String targetUserId);
}
