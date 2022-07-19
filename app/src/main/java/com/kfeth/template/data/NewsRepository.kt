package com.kfeth.template.data

import androidx.room.withTransaction
import com.kfeth.template.api.ArticleResponse
import com.kfeth.template.api.NewsApi
import com.kfeth.template.api.mapToEntity
import com.kfeth.template.di.IoDispatcher
import com.kfeth.template.util.Result
import com.kfeth.template.util.networkBoundResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val api: NewsApi,
    private val dao: NewsDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {
    fun getNewsStream(): Flow<List<Article>> {
        return dao.getAllArticles().map { entities ->
            entities.map(ArticleEntity::asExternalModel)
        }.onEach {
            if (it.isEmpty()) {
                refreshNews()
            }
        }
    }

    suspend fun refreshNews() {
        // todo reduce this
        delay(2000)
        api.getHeadlines()
            .also { response ->
                dao.deleteAndInsert(articles = response.articles.map(ArticleResponse::mapToEntity))
            }
    }

    fun getArticle(articleId: String): Flow<ArticleEntity> =
        dao.getArticle(articleId)

    // todo clean this
//    fun getHeadlines(): Flow<Result<List<ArticleEntity>>> = networkBoundResource(
//        query = { dao.getAllArticles() },
//        fetch = {
//            delay(500)
//            api.getHeadlines().articles
//        },
//        saveFetchResult = { serverResp ->
//            val articles: List<ArticleEntity> = serverResp.map { it.mapToEntity() }
//            db.withTransaction {
//                dao.deleteAllArticles()
//                dao.insert(articles)
//            }
//        }
//    ).flowOn(ioDispatcher)
}
