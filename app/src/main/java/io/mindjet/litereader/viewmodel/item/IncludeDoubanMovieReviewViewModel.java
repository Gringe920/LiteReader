package io.mindjet.litereader.viewmodel.item;

import android.databinding.ObservableField;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import io.mindjet.jetgear.mvvm.base.BaseViewModel;
import io.mindjet.jetgear.mvvm.viewinterface.ViewInterface;
import io.mindjet.litereader.R;
import io.mindjet.litereader.databinding.IncludeDoubanReviewBinding;
import io.mindjet.litereader.http.SimpleHttpSubscriber;
import io.mindjet.litereader.model.item.douban.Review;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 通用长影评详情 view model
 * <p>
 * Created by Mindjet on 2017/4/6.
 */

public class IncludeDoubanMovieReviewViewModel extends BaseViewModel<ViewInterface<IncludeDoubanReviewBinding>> {

    private final String PREFIX = "https://movie.douban.com/review/";
    public ObservableField<String> content;
    private Review review;

    public IncludeDoubanMovieReviewViewModel(Review review) {
        this.review = review;
    }

    public Review getReview() {
        return review;
    }

    @Override
    public int getLayoutId() {
        return R.layout.include_douban_review;
    }

    @Override
    public void onViewAttached(View view) {
        content = new ObservableField<>(getString(R.string.loading));
        decodeHtml(review.id);
    }

    private void decodeHtml(final String id) {
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return extractContent(id);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SimpleHttpSubscriber<String>() {
                    @Override
                    public void onNext(String html) {
                        content.set(html);
                    }
                });

    }

    private String extractContent(String id) {
        String result = getString(R.string.load_fail);
        try {
            Document doc = Jsoup.connect(PREFIX + id).get();
            Element content = doc.getElementsByClass("review-content").first();
            result = content.html();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
