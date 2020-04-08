package net.cararea.inspector;

import android.content.Context;
import android.os.AsyncTask;

import net.cararea.inspector.db.AppDatabase;
import net.cararea.inspector.db.Setting;
import net.cararea.inspector.db.SettingDao;

import java.util.HashMap;

import androidx.lifecycle.LiveData;

public class SettingRepository {
    private SettingDao dao;
    private HashMap<String, LiveData<Setting>> settings = new HashMap<>();

    SettingRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        dao = db.settingDao();
    }

    LiveData<Setting> get(String name) {
        if (!settings.containsKey(name)) {
            settings.put(name, dao.getByName(name));
        }
        return settings.get(name);
    }

    public void insert(Setting... settings) {
        new insertAsyncTask(dao).execute(settings);
    }

    public void update(Setting setting) {
        new updateAsyncTask(dao).execute(setting);
    }

    private static class insertAsyncTask extends AsyncTask<Setting, Void, Void> {
        private SettingDao mAsyncTaskDao;

        insertAsyncTask(SettingDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Setting... params) {
            mAsyncTaskDao.insertAll(params);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<Setting, Void, Void> {
        private SettingDao mAsyncTaskDao;

        updateAsyncTask(SettingDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Setting... params) {
            mAsyncTaskDao.updateAll(params);
            return null;
        }
    }
}