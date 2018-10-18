package com.dazito.oauthexample.dao;

import com.dazito.oauthexample.model.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {

    Optional<Channel> findById(Long id);

    Long countByAccountEntityList_Id(Long id);

    Long countByAccountEntityList_IdAndId(Long accountId, Long id);

}
