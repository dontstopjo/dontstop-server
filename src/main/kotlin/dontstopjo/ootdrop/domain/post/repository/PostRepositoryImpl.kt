package dontstopjo.ootdrop.domain.post.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import dontstopjo.ootdrop.domain.post.entity.Post
import dontstopjo.ootdrop.domain.post.entity.QPost.post
import dontstopjo.ootdrop.domain.post.entity.QPostSubStyle.postSubStyle
import dontstopjo.ootdrop.domain.post.enums.MainStyle
import dontstopjo.ootdrop.domain.post.enums.SubStyle
import org.springframework.stereotype.Repository

@Repository
class PostRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : PostRepositoryCustom {

    override fun findPostsByCriteria(
        keyword: String?,
        mainStyle: MainStyle?,
        subStyles: List<SubStyle>?
    ): List<Post> {
        return queryFactory
            .selectFrom(post)
            .leftJoin(postSubStyle).on(post.id.eq(postSubStyle.post.id))
            .where(
                keywordContains(keyword),
                mainStyleEq(mainStyle),
                subStylesIn(subStyles)
            )
            .distinct()
            .fetch()
    }

    private fun keywordContains(keyword: String?): BooleanExpression? {
        return keyword?.let {
            post.title.containsIgnoreCase(it)
                .or(post.content.containsIgnoreCase(it))
        }
    }

    private fun mainStyleEq(mainStyle: MainStyle?): BooleanExpression? {
        return mainStyle?.let { post.mainStyle.eq(it) }
    }

    private fun subStylesIn(subStyles: List<SubStyle>?): BooleanExpression? {
        return subStyles?.takeIf { it.isNotEmpty() }?.let { postSubStyle.subStyle.`in`(it) }
    }
}
