package com.example.newversiontest;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidParameterException;

 public class FirstFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> ,
        CursorRecyclerViewAdapter.OnTaskClickListener {
    private static final String TAG = "MainActivityFragment";

    public static final int LOADER_ID = 0;

    private CursorRecyclerViewAdapter mAdapter; // add adapter reference

     private Timing mCurrentTiming = null;

    public FirstFragment() {
        Log.d(TAG, "MainActivityFragment: starts");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated: starts");
        super.onActivityCreated(savedInstanceState);

        // Activities containing this fragment must implement its callbacks.
        Activity activity = getActivity();
        if(!(activity instanceof CursorRecyclerViewAdapter.OnTaskClickListener)) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement CursorRecyclerViewAdapter.OnTaskClickListener interface");
        }

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onEditClick(@NonNull Task task) {
        Log.d(TAG, "onEditClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity();
        if(listener != null) {
            listener.onEditClick(task);
        }
    }

    @Override
    public void onDeleteClick(@NonNull Task task) {
        Log.d(TAG, "onDeleteClick: called");
        CursorRecyclerViewAdapter.OnTaskClickListener listener = (CursorRecyclerViewAdapter.OnTaskClickListener) getActivity();
        if(listener != null) {
            listener.onDeleteClick(task);
        }
    }

     @Override
     public void onTaskLongClick(Task task) {
         Log.d(TAG, "onTaskLongClick: called");
         Toast.makeText(getActivity(), "Task " + task.getId() + " clicked", Toast.LENGTH_SHORT).show();
         TextView taskName = getActivity().findViewById(R.id.current_task);
         if (mCurrentTiming != null) {
             if (task.getId() == mCurrentTiming.getTask().getId()) {
                 // the current task was tapped a second time, so stop timing
                 saveTiming(mCurrentTiming);
                 mCurrentTiming = null;
                 taskName.setText(getString(R.string.no_task_message));
             } else {
                 // a new task is being timed, so stop the old one first
                 saveTiming(mCurrentTiming);
                 mCurrentTiming = new Timing(task);
                 taskName.setText("Timing " + mCurrentTiming.getTask().getName());
             }
         } else {
             // no task being timed, so start timing the new task
             mCurrentTiming = new Timing(task);
             taskName.setText("Timing " + mCurrentTiming.getTask().getName());
         }
     }
     @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: starts");
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.task_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if(mAdapter == null) {
            mAdapter = new CursorRecyclerViewAdapter(null, this);
        }
//        } else {
//            mAdapter.setListener((CursorRecyclerViewAdapter.OnTaskClickListener) getActivity());
//        }
        recyclerView.setAdapter(mAdapter);

        Log.d(TAG, "onCreateView: returning");
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: starts with id " + id);
        String[] projection = {TasksContract.Columns._ID, TasksContract.Columns.TASKS_NAME,
                TasksContract.Columns.TASKS_DESCRIPTION, TasksContract.Columns.TASKS_SORTORDER};
        // <order by> Tasks.SortOrder, Tasks.Name COLLATE NOCASE
        String sortOrder = TasksContract.Columns.TASKS_SORTORDER + "," + TasksContract.Columns.TASKS_NAME + " COLLATE NOCASE";

        switch(id) {
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        TasksContract.CONTENT_URI,
                        projection,
                        null,
                        null,
                        sortOrder);
            default:
                throw new InvalidParameterException(TAG + ".onCreateLoader called with invalid loader id" + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "Entering onLoadFinished");
        mAdapter.swapCursor(data);
        int count = mAdapter.getItemCount();

        Log.d(TAG, "onLoadFinished: count is " + count);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset: starts");
        mAdapter.swapCursor(null);
    }
    void saveTiming(Timing currentTiming){
        currentTiming.setDuration();
        if(currentTiming != null) {
            ContentResolver contentResolver = getActivity().getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(TimingsContract.Columns.TIMINGS_TASK_ID, currentTiming.getTask().getId());
            contentValues.put(TimingsContract.Columns.TIMINGS_START_TIME, currentTiming.getStartTime());
            contentValues.put(TimingsContract.Columns.TIMINGS_DURATION, currentTiming.getDuration());

            contentResolver.insert(TimingsContract.CONTENT_URI, contentValues);
            Log.d(TAG, "saveCurrentTiming: Exiting saveTiming()");
        }
    }
}