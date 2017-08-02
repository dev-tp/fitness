package cat.dev.fitness;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class WorkoutHistoryFragment extends Fragment {

    private WorkoutAdapter mWorkoutAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mWorkoutAdapter = new WorkoutAdapter(getActivity());

        View view = inflater.inflate(R.layout.fragment_workout_history, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.workouts);
        recyclerView.setAdapter(mWorkoutAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mWorkoutAdapter.deleteEntry((long) viewHolder.itemView.getTag());
            }
        };

        new ItemTouchHelper(callback).attachToRecyclerView(recyclerView);

        FloatingActionButton startNewWorkoutButton = (FloatingActionButton) view.findViewById(
                R.id.new_workout_button);
        startNewWorkoutButton.setSize(FloatingActionButton.SIZE_NORMAL);
        startNewWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivity(new Intent(getContext(), GoogleMapsActivity.class));
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        mWorkoutAdapter.onDestroy();

        super.onDestroy();
    }
}
