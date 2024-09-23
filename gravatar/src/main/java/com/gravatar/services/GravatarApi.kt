package com.gravatar.services

import com.gravatar.restapi.apis.AvatarsApi
import com.gravatar.restapi.apis.ProfilesApi

internal interface GravatarApi : ProfilesApi, AvatarsApi
