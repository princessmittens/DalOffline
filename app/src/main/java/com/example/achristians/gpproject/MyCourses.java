package com.example.achristians.gpproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/*
 *This class displays a list of courses that the individual user is currently enrolled in
 */

public class MyCourses extends Menu {

    //Full list of courses from DB
    ArrayAdapter<Course> arrayAdapter;

    /**
     * Basic activity functionality on launch
     * Shows list of courses the user is currently registered for
     * @param savedInstanceState : app context passed to activity on creation
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_courses);
        final ListView courseListView = findViewById(R.id.myCourseListView);

        Course.courses = new ArrayList<>();
        Listing.listings = new ArrayList<>();

        fetchCourses();

        //Add data to courseListView
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Course.courses);
        courseListView.setAdapter(arrayAdapter);

        courseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * Handles event when user clicks on an item in the list, displays course details for selected item
             *
             * @param parent : selected AdapterView
             * @param view : current view context
             * @param position : position of clicked item in list
             * @param id : course identifier for Firebase
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyCourses.this, CourseDetails.class);

                Course clicked = Course.courses.get(position);
                ArrayList<Listing> availableListings = new ArrayList<Listing>();

                ArrayList<Integer> listingNum = new ArrayList<Integer>();
                int index = 0;
                for(Listing L : Listing.listings){
                    if(L.Key.equals(clicked.Key)){
                        availableListings.add(L);
                        listingNum.add(index);
                    }
                    index++;
                }

                intent.putExtra("Course", clicked);
                intent.putExtra("Listings", availableListings);
                intent.putExtra("Listings index", listingNum);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Fetches course information from backing db on startup, no filtering/searching
     *
     */
    public void fetchCourses(){
        DatabaseReference coursesDataReference = Firebase.getRootDataReference().child("Courses/");

        coursesDataReference.addValueEventListener(
                new ValueEventListener() {
                    /**
                     * Updates list when changes occur in database
                     *
                     * @param dataSnapshot : returned data segment from Firebase
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<Course> inputCourses = new ArrayList<>();
                        Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                        HashMap<String, String> toShow = User.getUser().getRegistered();

                        for (DataSnapshot dsCourse: dataSnapshots) {
                            Course currentCourse = dsCourse.getValue(Course.class);
                            for(HashMap.Entry<String, String> userCourse : toShow.entrySet()){
                                if(currentCourse.Key.equals(userCourse.getKey())){
                                    inputCourses.add(currentCourse);
                                }
                            }
                        }

                        if(Course.courses == null){
                            Course.courses = new ArrayList<>();
                        }
                        courseChangeHandler(inputCourses);
                    }

                    /**
                     * Error handling for fetching information from database
                     *
                     * @param databaseError : returned error from Firebase
                     */
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("Error",databaseError.toString());
                    }
                }
        );

        DatabaseReference listingsDataReference = Firebase.getRootDataReference().child("Listings/");

        listingsDataReference.addValueEventListener(
                new ValueEventListener() {
                    /**
                     * Updates listings in case of data change
                     *
                     * @param dataSnapshot : returned data from Firebase
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterable<DataSnapshot> dataSnapshots = dataSnapshot.getChildren();
                        ArrayList<Listing> listingsList = new ArrayList<>();

                        for (DataSnapshot dsListing: dataSnapshots) {
                            listingsList.add(dsListing.getValue(Listing.class));
                        }

                        if(Listing.listings == null){
                            Listing.listings = new ArrayList<>();
                        }

                        Listing.listings.clear();
                        Listing.listings.addAll(listingsList);
                    }

                    /**
                     * Error handling for listing data update
                     *
                     * @param databaseError : returned error from Firebase
                     */
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("Error",databaseError.toString());
                    }
                }
        );
    }

    /**
     * Handles the event of course data being pushed to the application from an external source
     * (The DB). Extracted from the value event listener so functionality/inputs can be mocked for
     * testing.
     * @param courseListNew : A new arraylist of courses to display
     */
    public void courseChangeHandler(ArrayList<Course> courseListNew){
        //Emptying the course list, as anytime data is changed db side this method will
        //be called, and add all elements to the end of the list
        Course.courses.clear();
        Course.courses.addAll(courseListNew);
        arrayAdapter.notifyDataSetChanged();
    }
    
    @Override
    public void onBackPressed(){
        childBackPressed();
    }
}