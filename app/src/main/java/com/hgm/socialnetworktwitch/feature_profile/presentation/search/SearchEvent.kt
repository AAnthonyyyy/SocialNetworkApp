package com.hgm.socialnetworktwitch.feature_profile.presentation.search


sealed class SearchEvent {
      data class Query(val query: String): SearchEvent()
      data class UpdateFollowState(val userId: String): SearchEvent()
}
