package com.kirakishou.mvrxtest.data

import com.kirakishou.mvrxtest.data.response.ColorResponse
import com.kirakishou.mvrxtest.data.response.MyColor
import io.reactivex.Single
import java.util.*
import java.util.concurrent.TimeUnit

class FakeApiService : ApiService {
  private val random = Random()

  override fun fetchNextPage(lastId: Long, count: Int): Single<List<ColorResponse>> {
    return Single.fromCallable {
      val startIndex = lastId + 1

      return@fromCallable (startIndex until (startIndex + count))
        .map { id -> ColorResponse(id, MyColor.create(random)) }
    }.delay(300, TimeUnit.MILLISECONDS)
  }
}