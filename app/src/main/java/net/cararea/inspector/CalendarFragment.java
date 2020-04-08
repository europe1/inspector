package net.cararea.inspector;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class CalendarFragment extends Fragment {
    private CompactCalendarView compactCalendarView;
    private boolean isSelectionColorChanged = false;

    private OnFragmentInteractionListener listener;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        compactCalendarView = view.findViewById(R.id.calendar_view);

        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setIsRtl(false);
        compactCalendarView.displayOtherMonthDays(false);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                listener.onSelectDay(dateClicked);
            }

            @Override
            public void onMonthScroll(Date firstDayOfMonth) {
                listener.onSelectMonth(firstDayOfMonth);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set to current day on resume to set calendar to latest day
        // toolbar.setTitle(dateFormatForMonth.format(new Date()));
    }

    void addEvents(List<Event> events) {
        compactCalendarView.removeAllEvents();
        compactCalendarView.addEvents(events);
        compactCalendarView.invalidate();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    void setSelectionColor(int color) {
        compactCalendarView.setCurrentSelectedDayBackgroundColor(color);
        isSelectionColorChanged = true;
    }

    void restoreSelectionColor(Context context) {
        if (isSelectionColorChanged) {
            compactCalendarView.setCurrentSelectedDayBackgroundColor(ContextCompat.getColor(
                    context, R.color.colorPrimary));
            isSelectionColorChanged = false;
        }
    }

    public interface OnFragmentInteractionListener {
        void onSelectDay(Date day);
        void onSelectMonth(Date month);
    }
}
