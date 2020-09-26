package com.techbeloved.hymnbook.data

import android.net.Uri
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.techbeloved.hymnbook.BuildConfig
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.utils.DYNAMIC_LINK_DOMAIN
import com.techbeloved.hymnbook.utils.appendHymnId
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class ShareLinkProvider @Inject constructor(private val dynamicLinks: FirebaseDynamicLinks,
                                            @Named("DYNAMIC_LINK_DOMAIN") private val dynamicLinkDomain: String,
                                            @Named("APP_ID") private val appPackageName: String) {

    val shareLinkProvider: ShareLinkProvider by lazy {
        ShareLinkProvider(FirebaseDynamicLinks.getInstance(), DYNAMIC_LINK_DOMAIN, BuildConfig.APPLICATION_ID)
    }

    /**
     * Builds short link for firebase sharing
     * @param browsingCategoryUri the category from where the item can be located. It must not be playlist as playlist is a user generated content
     */
    fun getShortLinkForItem(hymn: Hymn,
                            browsingCategoryUri: String,
                            description: String,
                            minimumVersion: Int,
                            logoUrl: String): Single<String> {

        return Single.create { emitter ->

            dynamicLinks.createDynamicLink()
                    .setLink(Uri.parse(browsingCategoryUri.appendHymnId(hymn.num)))
                    .setDomainUriPrefix(dynamicLinkDomain)
                    .setAndroidParameters(
                            DynamicLink.AndroidParameters.Builder(appPackageName)
                                    .setMinimumVersion(minimumVersion)
                                    .build())
                    .setSocialMetaTagParameters(
                            DynamicLink.SocialMetaTagParameters.Builder()
                                    .setTitle("${hymn.title}, Hymn ${hymn.num}")
                                    .setDescription(description)
                                    .setImageUrl(Uri.parse(logoUrl))
                                    .build())
                    .buildShortDynamicLink()
                    .addOnSuccessListener { shortDynamicLink ->
                        Timber.i("shortLink: %s", shortDynamicLink.shortLink)
                        if (!emitter.isDisposed)
                            emitter.onSuccess(shortDynamicLink.shortLink.toString())
                    }
                    .addOnFailureListener { emitter.tryOnError(it) }
                    .addOnCanceledListener { emitter.tryOnError(Throwable("Operation cancelled!")) }
        }
    }
}