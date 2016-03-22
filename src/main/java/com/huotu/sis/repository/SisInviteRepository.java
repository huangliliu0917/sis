package com.huotu.sis.repository;

import com.huotu.sis.entity.SisInviteLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by lgh on 2015/11/9.
 */

@Repository
public interface SisInviteRepository extends JpaRepository<SisInviteLog, Long> {
    /**
     * 统计某个人邀请某个人的记录数
     * @param acceptId
     * @return
     */
    @Query("select count(log) from SisInviteLog log where  log.acceptId=?1 and log.inviterId=?2")
    Long countByAcceptIdAndInviterId(Long acceptId, Long inviterId);

    Long countByAcceptId(Long acceptId);


    /**
     * 查找用户最近填写的开店邀请记录
     * @param acceptId      接受邀请的会员的ID
     * @return
     */
    SisInviteLog findFirstByAcceptIdOrderByAcceptTimeDesc(Long acceptId);
}
