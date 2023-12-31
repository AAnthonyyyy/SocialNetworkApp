package com.hgm.socialnetworktwitch.feature_profile.data.dto

import com.hgm.socialnetworktwitch.feature_profile.domain.model.Profile

data class ProfileResponse(
    val userId:String,
    val username: String,
    val profilePictureUrl: String,
    val bannerUrl:String?,
    val bio: String,//个人履历
    val topSkillUrls: List<SkillDto>,
    val githubUrl: String?,
    val instagramUrl: String?,
    val linkedInUrl: String?,
    val followingCount: Int,//关注量
    val followedCount: Int,//粉丝量
    val postCount: Int,
    val isOwnProfile: Boolean,
    val isFollowing: Boolean
){
    fun toProfile(): Profile {
        return Profile(
            userId = userId,
            username = username,
            bio = bio,
            followingCount = followingCount,
            followedCount = followedCount,
            postCount = postCount,
            profilePictureUrl = profilePictureUrl,
            bannerUrl = bannerUrl,
            topSkills = topSkillUrls.map { it.toSkill() },
            gitHubUrl = githubUrl,
            instagramUrl = instagramUrl,
            linkedInUrl = linkedInUrl,
            isOwnProfile = isOwnProfile,
            isFollowing = isFollowing
        )
    }
}
