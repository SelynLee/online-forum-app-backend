package com.beaconfire.posts_service.domain;

import java.util.Date;
import java.util.List;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubReply {
	
	private String subReplyId;
    private Integer userId;
    private String comment;
    // for deleting the reply
    private boolean isActive;
    private Date created_at;
    private Date updated_at;

}
