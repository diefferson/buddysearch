package com.buddysearch.presentation.domain.interactor;

import com.buddysearch.presentation.domain.executor.PostExecutionThread;
import com.buddysearch.presentation.domain.executor.ThreadExecutor;
import com.buddysearch.presentation.domain.repository.Repository;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public abstract class UseCase<DATA, REPOSITORY extends Repository> {

    final REPOSITORY repository;

    private final ThreadExecutor threadExecutor;

    private final PostExecutionThread postExecutionThread;

    private Subscription subscription = Subscriptions.empty();

    public UseCase(REPOSITORY repository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.repository = repository;
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
    }

    protected abstract Observable<DATA> buildUseCaseObservable();

    public void execute(Subscriber<DATA> useCaseSubscriber) {
        this.subscription = this.buildUseCaseObservable()
                .subscribeOn(Schedulers.from(threadExecutor))
                .observeOn(postExecutionThread.getScheduler())
                .subscribe(useCaseSubscriber);
    }

    public boolean isUnsubscribed() {
        return subscription.isUnsubscribed();
    }

    public void unsubscribe() {
        if (!isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}