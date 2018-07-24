package com.paybook.sync.data

import com.paybook.core.Repository
import com.paybook.sync.SyncModule
import com.paybook.sync.SyncService
import com.paybook.sync.entities.Organization
import com.paybook.sync.models.SyncResult
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject

/**
 * Created by Gerardo Teruel on 5/30/18.
 */
class OrganizationsRepository(
  private val syncService: SyncService,
  private val sitesRepository: SitesRepository
) : Repository<Organization, String> {

  companion object {
    val cache: MutableList<Organization> = mutableListOf()
  }

  private val subject = PublishSubject.create<List<Organization>>()

  override fun add(
    value: Organization,
    key: String
  ) {
    cache.add(value)
    subject.onNext(cache)
  }

  override fun get(key: String): Single<Organization> {
    return Single.defer {
      val entry = cache.find { it.id == key }
      if (entry == null) {
        fetch(key)
      } else {
        Single.just(entry)
      }
    }
  }

  override fun fetch(key: String): Single<Organization> {
    return fetch().map {
      it.first { it.id == key }
    }
  }

  override fun get(): Single<List<Organization>> {
    return if (cache.isEmpty()) {
      fetch()
    } else {
      Single.just(cache)
    }
  }

  override fun fetch(): Single<List<Organization>> {
    return Single.defer {
      return@defer syncService.organizations(false)
          .map { SyncResult(it) }
          .flatMap {
            if (it.isSuccess) {
              Single.just(it.body()!!.map())
            } else {
              Single.error { it.error() }
            }
          }
          .flatMap { organizations ->
            if (SyncModule.isTest) {
              syncService.organizations(true)
                  .map { SyncResult(it) }
                  .flatMap {
                    if (it.isSuccess) {
                      Single.just(organizations + it.body()!!.map())
                    } else {
                      Single.error { it.error() }
                    }
                  }
            } else {
              Single.just(organizations)
            }
          }
          .doOnEvent { organizations, _ ->
            organizations?.let {
              it.forEach {
                it.sites.forEach { sitesRepository.add(it, it.id) }
              }
            }
          }
    }
  }

  override fun stream(): Observable<List<Organization>> {
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
    cache.clear()
    return Completable.complete()
  }

  override fun clear(key: String): Completable {
    return clearMemory(key)
  }

  override fun clear(): Completable {
    return clearMemory()
  }

}
