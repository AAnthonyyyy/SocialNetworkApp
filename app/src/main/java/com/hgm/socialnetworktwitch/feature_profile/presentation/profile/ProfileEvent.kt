package com.hgm.socialnetworktwitch.feature_profile.presentation.profile

/**
 * @auth：HGM
 * @date：2023-10-20 17:39
 * @desc：
 */
sealed class ProfileEvent{
      data class GetProfile(val userId:String):ProfileEvent()
}