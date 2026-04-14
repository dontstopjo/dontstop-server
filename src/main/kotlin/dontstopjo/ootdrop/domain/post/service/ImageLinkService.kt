package dontstopjo.ootdrop.domain.post.service

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Service

@Service
class ImageLinkService {

    fun extractRepresentativeImage(url: String): String? {
        return try {
            val document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .header("Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7")
                .timeout(5000)
                .get()

            // 1순위: Open Graph & Twitter Card (디코/카톡 방식)
            val metaImage = extractMetaImage(document)
            if (metaImage != null) return metaImage

            // 2순위: JSON-LD (구조화 데이터 - 쇼핑몰/블로그에서 매우 정확함)
            val jsonLdImage = extractJsonLdImage(document)
            if (jsonLdImage != null) return jsonLdImage

            // 3순위: 본문 내 점수 기반 베스트 이미지
            val bestBodyImage = extractBestBodyImage(document)
            if (bestBodyImage != null) return bestBodyImage

            // 4순위: 최후의 수단 (고해상도 파비콘/아이콘)
            extractFavicon(document)

        } catch (e: Exception) {
            null
        }
    }

    private fun extractMetaImage(doc: Document): String? {
        val selectors = listOf(
            "meta[property=og:image]",
            "meta[name=twitter:image]",
            "meta[name=image]",
            "link[rel=image_src]"
        )
        for (selector in selectors) {
            val url = doc.select(selector).attr("abs:content").ifBlank { doc.select(selector).attr("abs:href") }
            if (url.isNotBlank()) return url
        }
        return null
    }

    private fun extractJsonLdImage(doc: Document): String? {
        val scripts = doc.select("script[type=application/ld+json]")
        for (script in scripts) {
            val content = script.data()
            // 정규식으로 간단하게 "image": "URL" 추출 (Jackson 사용 권장)
            val regex = """"image"\s*:\s*"([^"]+)"""".toRegex()
            val match = regex.find(content)
            if (match != null) return match.groupValues[1]
        }
        return null
    }

    private fun extractBestBodyImage(doc: Document): String? {
        val images = doc.select("img")
        var bestImg: String? = null
        var maxScore = -100

        for (img in images) {
            val src = img.attr("abs:src")
            if (src.isBlank() || src.contains("data:image")) continue

            var score = 0
            val alt = img.attr("alt").lowercase()
            val className = img.className().lowercase()

            // 가산점 로직
            if (alt.contains("상품") || alt.contains("product") || alt.contains("main")) score += 30
            if (className.contains("representative") || className.contains("primary")) score += 40

            // 감점 로직 (노이즈 제거)
            if (src.contains("logo") || src.contains("icon") || src.contains("banner")) score -= 50
            if (src.endsWith(".gif") || src.endsWith(".svg")) score -= 100

            if (score > maxScore) {
                maxScore = score
                bestImg = src
            }
        }
        return if (maxScore > -10) bestImg else null
    }

    private fun extractFavicon(doc: Document): String? {
        val iconSelectors = listOf(
            "link[rel~=(apple-touch-icon|shortcut|icon)]"
        )
        return iconSelectors.map { doc.select(it).attr("abs:href") }
            .firstOrNull { it.isNotBlank() }
    }
}