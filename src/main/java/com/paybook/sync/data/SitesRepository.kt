package com.paybook.sync.data

import com.paybook.core.Repository
import com.paybook.sync.entities.Site
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

/**
 * Created by Gerardo Teruel on 5/30/18.
 */
class SitesRepository : Repository<Site, String> {

  companion object {
    val cache: MutableList<Site> = mutableListOf()
    val subject: PublishSubject<List<Site>> = PublishSubject.create()
  }

  override fun add(
    value: Site,
    key: String
  ) {
    cache.add(value)
  }

  override fun get(key: String): Single<Site> {
    return get().map { it.first { it.id == key } } as Single<Site>
  }

  override fun fetch(key: String): Single<Site> {
    TODO("not implemented")
  }

  override fun get(): Single<List<Site>> {
    return Single.just(cache)
  }

  override fun fetch(): Single<List<Site>> {
    TODO("not implemented")
  }

  override fun stream(): Observable<List<Site>> {
    return subject.hide()
  }

  override fun clearMemory(key: String): Completable {
    val index = cache.indexOfFirst { it.id == key }
    if (index == -1) {
      return Completable.error(IllegalStateException("Paybook $key not in cache"))
    } else {
      cache.removeAt(index)
    }

    return Completable.complete()
  }

  override fun clearMemory(): Completable {
    OrganizationsRepository.cache.clear()
    return Completable.complete()
  }

  override fun clear(key: String): Completable {
    TODO("not implemented")
  }

  override fun clear(): Completable {
    TODO("not implemented")
  }

}
