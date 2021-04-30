package me.modernpage.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.modernpage.entity.Group;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDetailResponse {
	private Group group;
	private long total_members_count;
}
