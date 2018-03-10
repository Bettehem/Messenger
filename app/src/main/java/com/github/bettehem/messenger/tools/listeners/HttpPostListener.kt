package com.github.bettehem.messenger.tools.listeners

import org.apache.http.HttpResponse

interface HttpPostListener{
    fun onPostResponse(response : HttpResponse)
}