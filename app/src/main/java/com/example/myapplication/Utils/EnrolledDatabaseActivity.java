package com.example.myapplication.Utils;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.R;
import com.wf.wffrsinglecamapp;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EnrolledDatabaseActivity extends AppCompatActivity {
    EnrolledUserAdapter enrolledUserAdapter;
    RecyclerView recyclerView;
    String assetPath;
    List<EnrolledDatabaseObject> enrolledDatabaseObjectList;
    LinearLayout enrolled_linear;
    SearchView searchView;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrolled_database);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Search Person");
        myToolbar.setTitleTextColor(Color.WHITE);
        myToolbar.setTitleMarginStart(210);
        setSupportActionBar(myToolbar);
        myToolbar.setNavigationIcon(R.drawable.back_button);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                    return;
                }
                finish();
            }
        });

        sharedPreferences = getSharedPreferences("attendance_prefrence", Context.MODE_PRIVATE);
        enrolled_linear = (LinearLayout) findViewById(R.id.enrolled_dabatase_list_linear);
        assetPath = (new AssetFileManager(EnrolledDatabaseActivity.this).getFilePath()) + "/";
        recyclerView = (RecyclerView) findViewById(R.id.enrolled_user_listview);
        enrolledDatabaseObjectList = new ArrayList<>();
        initDatabaseList();
        boolean isDebug = sharedPreferences.getBoolean("debug_switch",false);

        enrolledUserAdapter = new EnrolledUserAdapter(this, enrolledDatabaseObjectList,
                false,true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(enrolledUserAdapter);
        enrolledUserAdapter.notifyDataSetChanged();

//        SwipeHelper swipeHelper = new SwipeHelper(this, recyclerView) {
//            @Override
//            public void instantiateUnderlayButton(RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons) {
//                underlayButtons.add(new SwipeHelper.UnderlayButton(
//                        "Delete",
//                        0,
//                        Color.parseColor("#1CB5E0"),
//                        new SwipeHelper.UnderlayButtonClickListener() {
//                            @Override
//                            public void onClick(int pos) {
//                                // TODO: onDelete
//                                showDeleteDialog(pos,enrolledUserAdapter.getFilteredenrolledDatabaseObjectList().
//                                        get(pos).getImage_id());
//                            }
//                        }
//                ));
//            }
//        };

//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHelper);
//        itemTouchHelper.attachToRecyclerView(recyclerView);

    }

    private void showDeleteDialog(final int position,final int recordID){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Delete Record!!");

        // Setting Dialog Message
        alertDialog.setMessage("Are you sure you want Delete this Record?");

        // Setting Icon to Dialog

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                // Write your code here to invoke YES event

                wffrsinglecamapp.deletePerson(recordID);
                Toast.makeText(EnrolledDatabaseActivity.this, "Record Deleted", Toast.LENGTH_SHORT).show();
                enrolledUserAdapter.removeItem(position);
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to invoke NO event
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void initDatabaseList() {
        wffrsinglecamapp.releaseEngine();
        Log.i("SDSD","INIT : "+wffrsinglecamapp.getDatabase());
        String[] names = (String[]) wffrsinglecamapp.getDatabaseNames();
        int[] recordIDs = (int[]) wffrsinglecamapp.getDatabaseRecords();

        for (int i = 0; i < names.length; i++) {
            String name_without_contact = names[i];
            enrolledDatabaseObjectList.add(new EnrolledDatabaseObject(printDir(assetPath +
                    "wffrdb/pid" + recordIDs[i]),name_without_contact, recordIDs[i]));
        }
        Collections.sort(enrolledDatabaseObjectList,new Comparator<EnrolledDatabaseObject>() {
            @Override
            public int compare(EnrolledDatabaseObject o1, EnrolledDatabaseObject o2) {
                return o1.getImage_name().compareToIgnoreCase(o2.getImage_name());
            }
        });
    }

    public static String printDir(String path) {
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files.length > 0) {
            return files[files.length - 1].getAbsolutePath();
        } else {
            return files[0].getAbsolutePath();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.enrolled_database_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                enrolledUserAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                enrolledUserAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }
}
