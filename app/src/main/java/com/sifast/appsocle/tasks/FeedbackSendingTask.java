package com.sifast.appsocle.tasks;

/**
 * Created by Asus on 14/07/2016.
 */


import android.app.Activity;
import android.os.AsyncTask;

import com.firebase.client.Firebase;
import com.sifast.appsocle.R;
import com.sifast.appsocle.models.Feedback;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Asus on 14/07/2016.
 */
public class FeedbackSendingTask extends AsyncTask {
    private Feedback feedback;
    private Activity activity;

    public FeedbackSendingTask(Feedback feedback, Activity activity) {
        this.activity = activity;
        this.feedback = feedback;
    }
    @Override
    protected Object doInBackground(Object[] params) {
        registerFeedback();
        return null;
    }
    public void registerFeedback() {
        String dbUrl =activity.getResources().getString(R.string.dbUrl);
        Firebase ref = new Firebase(dbUrl);
// Generate a reference to a new location and add some data using push()
        Firebase postRef = ref.child("Feedback");
        Firebase newPostRef = postRef.push();
// Add some data to the new location
        Map<String, String> post= new HashMap<String, String>();
        post.put("declaredBy", feedback.getDeclaredBy());
        post.put("declaredLong", feedback.getDeclaredLat());
        post.put("declaredLong", feedback.getDeclaredLat());
        post.put("messageDeclared", feedback.getMessageDeclared());
        post.put("declartionDate", feedback.getDeclartionDate());
        newPostRef.setValue(post);
    }
    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
