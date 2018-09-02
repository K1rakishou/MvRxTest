package com.kirakishou.mvrxtest.data

import com.kirakishou.mvrxtest.data.response.ColorResponse
import io.reactivex.Single

interface ApiService {
  fun fetchNextPage(lastId: Long, count: Int): Single<List<ColorResponse>>
}