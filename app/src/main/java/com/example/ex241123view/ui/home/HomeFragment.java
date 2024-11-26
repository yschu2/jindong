package com.example.ex241123view.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.ex241123view.databinding.FragmentHomeBinding;
//---------room--------
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.PrimaryKey;
import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    //-----------------room -------
    @Entity(tableName = "tbl_user")
    public class User {
        @PrimaryKey
        public int uid;
        public String firstName;
        public String lastName;
    }
   //---------------------
    @Dao
    public interface UserDao {
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        void insert(User user);
        @Update
        void update(User user);
        @Query("SELECT * from tbl_user ORDER By firstName Asc")
        LiveData<List<User>> getUser();
        @Query("DELETE from tbl_user")
        void deleteAll();
    }
    //--------------------------
    @Database(entities = {User.class}, version = 1, exportSchema = false)
    public abstract class UserRoomDB extends RoomDatabase {
        public abstract UserDao userDao();
        

        private static volatile UserRoomDB userRoomDB;
        private static final int NUMBER_OF_THREADS = 4;
        static final ExecutorService databaseWriteExecutor =
                Executors.newFixedThreadPool(NUMBER_OF_THREADS);

        static UserRoomDB getDatabase(final Context context) {
            if (userRoomDB == null) {
                synchronized (UserRoomDB.class) {
                    if (userRoomDB == null) {
                        userRoomDB = Room.databaseBuilder(context.getApplicationContext(),
                                        UserRoomDB.class, "student_database")
                                .build();
                    }
                }
            }
            return userRoomDB;
        }
    }
}