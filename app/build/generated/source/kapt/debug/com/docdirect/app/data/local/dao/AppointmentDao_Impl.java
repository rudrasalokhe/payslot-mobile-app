package com.docdirect.app.data.local.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.docdirect.app.data.local.entity.AppointmentEntity;
import com.docdirect.app.data.model.AppointmentStatus;
import java.lang.Class;
import java.lang.Exception;
import java.lang.IllegalArgumentException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class AppointmentDao_Impl implements AppointmentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AppointmentEntity> __insertionAdapterOfAppointmentEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateAppointmentStatus;

  private final SharedSQLiteStatement __preparedStmtOfAddPrescription;

  public AppointmentDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAppointmentEntity = new EntityInsertionAdapter<AppointmentEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `appointments` (`id`,`patientId`,`patientName`,`doctorId`,`doctorName`,`doctorSpecialty`,`appointmentDate`,`appointmentTime`,`symptoms`,`feePaid`,`status`,`transactionId`,`prescription`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AppointmentEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getPatientId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getPatientId());
        }
        if (entity.getPatientName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getPatientName());
        }
        if (entity.getDoctorId() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getDoctorId());
        }
        if (entity.getDoctorName() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getDoctorName());
        }
        if (entity.getDoctorSpecialty() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getDoctorSpecialty());
        }
        if (entity.getAppointmentDate() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getAppointmentDate());
        }
        if (entity.getAppointmentTime() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getAppointmentTime());
        }
        if (entity.getSymptoms() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getSymptoms());
        }
        statement.bindDouble(10, entity.getFeePaid());
        statement.bindString(11, __AppointmentStatus_enumToString(entity.getStatus()));
        if (entity.getTransactionId() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getTransactionId());
        }
        if (entity.getPrescription() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getPrescription());
        }
        statement.bindLong(14, entity.getCreatedAt());
      }
    };
    this.__preparedStmtOfUpdateAppointmentStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE appointments SET status = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfAddPrescription = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE appointments SET prescription = ?, status = 'COMPLETED' WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAppointment(final AppointmentEntity appointment,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAppointmentEntity.insert(appointment);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAppointmentStatus(final String appointmentId, final AppointmentStatus status,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateAppointmentStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, __AppointmentStatus_enumToString(status));
        _argIndex = 2;
        if (appointmentId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, appointmentId);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateAppointmentStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object addPrescription(final String appointmentId, final String prescription,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfAddPrescription.acquire();
        int _argIndex = 1;
        if (prescription == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, prescription);
        }
        _argIndex = 2;
        if (appointmentId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, appointmentId);
        }
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfAddPrescription.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AppointmentEntity>> getAppointmentsForDoctor(final String doctorId) {
    final String _sql = "SELECT * FROM appointments WHERE doctorId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (doctorId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, doctorId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"appointments"}, new Callable<List<AppointmentEntity>>() {
      @Override
      @NonNull
      public List<AppointmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfPatientName = CursorUtil.getColumnIndexOrThrow(_cursor, "patientName");
          final int _cursorIndexOfDoctorId = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorId");
          final int _cursorIndexOfDoctorName = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorName");
          final int _cursorIndexOfDoctorSpecialty = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorSpecialty");
          final int _cursorIndexOfAppointmentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentDate");
          final int _cursorIndexOfAppointmentTime = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentTime");
          final int _cursorIndexOfSymptoms = CursorUtil.getColumnIndexOrThrow(_cursor, "symptoms");
          final int _cursorIndexOfFeePaid = CursorUtil.getColumnIndexOrThrow(_cursor, "feePaid");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionId");
          final int _cursorIndexOfPrescription = CursorUtil.getColumnIndexOrThrow(_cursor, "prescription");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<AppointmentEntity> _result = new ArrayList<AppointmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppointmentEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpPatientId;
            if (_cursor.isNull(_cursorIndexOfPatientId)) {
              _tmpPatientId = null;
            } else {
              _tmpPatientId = _cursor.getString(_cursorIndexOfPatientId);
            }
            final String _tmpPatientName;
            if (_cursor.isNull(_cursorIndexOfPatientName)) {
              _tmpPatientName = null;
            } else {
              _tmpPatientName = _cursor.getString(_cursorIndexOfPatientName);
            }
            final String _tmpDoctorId;
            if (_cursor.isNull(_cursorIndexOfDoctorId)) {
              _tmpDoctorId = null;
            } else {
              _tmpDoctorId = _cursor.getString(_cursorIndexOfDoctorId);
            }
            final String _tmpDoctorName;
            if (_cursor.isNull(_cursorIndexOfDoctorName)) {
              _tmpDoctorName = null;
            } else {
              _tmpDoctorName = _cursor.getString(_cursorIndexOfDoctorName);
            }
            final String _tmpDoctorSpecialty;
            if (_cursor.isNull(_cursorIndexOfDoctorSpecialty)) {
              _tmpDoctorSpecialty = null;
            } else {
              _tmpDoctorSpecialty = _cursor.getString(_cursorIndexOfDoctorSpecialty);
            }
            final String _tmpAppointmentDate;
            if (_cursor.isNull(_cursorIndexOfAppointmentDate)) {
              _tmpAppointmentDate = null;
            } else {
              _tmpAppointmentDate = _cursor.getString(_cursorIndexOfAppointmentDate);
            }
            final String _tmpAppointmentTime;
            if (_cursor.isNull(_cursorIndexOfAppointmentTime)) {
              _tmpAppointmentTime = null;
            } else {
              _tmpAppointmentTime = _cursor.getString(_cursorIndexOfAppointmentTime);
            }
            final String _tmpSymptoms;
            if (_cursor.isNull(_cursorIndexOfSymptoms)) {
              _tmpSymptoms = null;
            } else {
              _tmpSymptoms = _cursor.getString(_cursorIndexOfSymptoms);
            }
            final double _tmpFeePaid;
            _tmpFeePaid = _cursor.getDouble(_cursorIndexOfFeePaid);
            final AppointmentStatus _tmpStatus;
            _tmpStatus = __AppointmentStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpTransactionId;
            if (_cursor.isNull(_cursorIndexOfTransactionId)) {
              _tmpTransactionId = null;
            } else {
              _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId);
            }
            final String _tmpPrescription;
            if (_cursor.isNull(_cursorIndexOfPrescription)) {
              _tmpPrescription = null;
            } else {
              _tmpPrescription = _cursor.getString(_cursorIndexOfPrescription);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new AppointmentEntity(_tmpId,_tmpPatientId,_tmpPatientName,_tmpDoctorId,_tmpDoctorName,_tmpDoctorSpecialty,_tmpAppointmentDate,_tmpAppointmentTime,_tmpSymptoms,_tmpFeePaid,_tmpStatus,_tmpTransactionId,_tmpPrescription,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AppointmentEntity>> getAppointmentsForPatient(final String patientId) {
    final String _sql = "SELECT * FROM appointments WHERE patientId = ? ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (patientId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, patientId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"appointments"}, new Callable<List<AppointmentEntity>>() {
      @Override
      @NonNull
      public List<AppointmentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfPatientName = CursorUtil.getColumnIndexOrThrow(_cursor, "patientName");
          final int _cursorIndexOfDoctorId = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorId");
          final int _cursorIndexOfDoctorName = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorName");
          final int _cursorIndexOfDoctorSpecialty = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorSpecialty");
          final int _cursorIndexOfAppointmentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentDate");
          final int _cursorIndexOfAppointmentTime = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentTime");
          final int _cursorIndexOfSymptoms = CursorUtil.getColumnIndexOrThrow(_cursor, "symptoms");
          final int _cursorIndexOfFeePaid = CursorUtil.getColumnIndexOrThrow(_cursor, "feePaid");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionId");
          final int _cursorIndexOfPrescription = CursorUtil.getColumnIndexOrThrow(_cursor, "prescription");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<AppointmentEntity> _result = new ArrayList<AppointmentEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AppointmentEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpPatientId;
            if (_cursor.isNull(_cursorIndexOfPatientId)) {
              _tmpPatientId = null;
            } else {
              _tmpPatientId = _cursor.getString(_cursorIndexOfPatientId);
            }
            final String _tmpPatientName;
            if (_cursor.isNull(_cursorIndexOfPatientName)) {
              _tmpPatientName = null;
            } else {
              _tmpPatientName = _cursor.getString(_cursorIndexOfPatientName);
            }
            final String _tmpDoctorId;
            if (_cursor.isNull(_cursorIndexOfDoctorId)) {
              _tmpDoctorId = null;
            } else {
              _tmpDoctorId = _cursor.getString(_cursorIndexOfDoctorId);
            }
            final String _tmpDoctorName;
            if (_cursor.isNull(_cursorIndexOfDoctorName)) {
              _tmpDoctorName = null;
            } else {
              _tmpDoctorName = _cursor.getString(_cursorIndexOfDoctorName);
            }
            final String _tmpDoctorSpecialty;
            if (_cursor.isNull(_cursorIndexOfDoctorSpecialty)) {
              _tmpDoctorSpecialty = null;
            } else {
              _tmpDoctorSpecialty = _cursor.getString(_cursorIndexOfDoctorSpecialty);
            }
            final String _tmpAppointmentDate;
            if (_cursor.isNull(_cursorIndexOfAppointmentDate)) {
              _tmpAppointmentDate = null;
            } else {
              _tmpAppointmentDate = _cursor.getString(_cursorIndexOfAppointmentDate);
            }
            final String _tmpAppointmentTime;
            if (_cursor.isNull(_cursorIndexOfAppointmentTime)) {
              _tmpAppointmentTime = null;
            } else {
              _tmpAppointmentTime = _cursor.getString(_cursorIndexOfAppointmentTime);
            }
            final String _tmpSymptoms;
            if (_cursor.isNull(_cursorIndexOfSymptoms)) {
              _tmpSymptoms = null;
            } else {
              _tmpSymptoms = _cursor.getString(_cursorIndexOfSymptoms);
            }
            final double _tmpFeePaid;
            _tmpFeePaid = _cursor.getDouble(_cursorIndexOfFeePaid);
            final AppointmentStatus _tmpStatus;
            _tmpStatus = __AppointmentStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpTransactionId;
            if (_cursor.isNull(_cursorIndexOfTransactionId)) {
              _tmpTransactionId = null;
            } else {
              _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId);
            }
            final String _tmpPrescription;
            if (_cursor.isNull(_cursorIndexOfPrescription)) {
              _tmpPrescription = null;
            } else {
              _tmpPrescription = _cursor.getString(_cursorIndexOfPrescription);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new AppointmentEntity(_tmpId,_tmpPatientId,_tmpPatientName,_tmpDoctorId,_tmpDoctorName,_tmpDoctorSpecialty,_tmpAppointmentDate,_tmpAppointmentTime,_tmpSymptoms,_tmpFeePaid,_tmpStatus,_tmpTransactionId,_tmpPrescription,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<AppointmentEntity> getAppointmentById(final String appointmentId) {
    final String _sql = "SELECT * FROM appointments WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (appointmentId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, appointmentId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"appointments"}, new Callable<AppointmentEntity>() {
      @Override
      @Nullable
      public AppointmentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPatientId = CursorUtil.getColumnIndexOrThrow(_cursor, "patientId");
          final int _cursorIndexOfPatientName = CursorUtil.getColumnIndexOrThrow(_cursor, "patientName");
          final int _cursorIndexOfDoctorId = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorId");
          final int _cursorIndexOfDoctorName = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorName");
          final int _cursorIndexOfDoctorSpecialty = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorSpecialty");
          final int _cursorIndexOfAppointmentDate = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentDate");
          final int _cursorIndexOfAppointmentTime = CursorUtil.getColumnIndexOrThrow(_cursor, "appointmentTime");
          final int _cursorIndexOfSymptoms = CursorUtil.getColumnIndexOrThrow(_cursor, "symptoms");
          final int _cursorIndexOfFeePaid = CursorUtil.getColumnIndexOrThrow(_cursor, "feePaid");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfTransactionId = CursorUtil.getColumnIndexOrThrow(_cursor, "transactionId");
          final int _cursorIndexOfPrescription = CursorUtil.getColumnIndexOrThrow(_cursor, "prescription");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final AppointmentEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpPatientId;
            if (_cursor.isNull(_cursorIndexOfPatientId)) {
              _tmpPatientId = null;
            } else {
              _tmpPatientId = _cursor.getString(_cursorIndexOfPatientId);
            }
            final String _tmpPatientName;
            if (_cursor.isNull(_cursorIndexOfPatientName)) {
              _tmpPatientName = null;
            } else {
              _tmpPatientName = _cursor.getString(_cursorIndexOfPatientName);
            }
            final String _tmpDoctorId;
            if (_cursor.isNull(_cursorIndexOfDoctorId)) {
              _tmpDoctorId = null;
            } else {
              _tmpDoctorId = _cursor.getString(_cursorIndexOfDoctorId);
            }
            final String _tmpDoctorName;
            if (_cursor.isNull(_cursorIndexOfDoctorName)) {
              _tmpDoctorName = null;
            } else {
              _tmpDoctorName = _cursor.getString(_cursorIndexOfDoctorName);
            }
            final String _tmpDoctorSpecialty;
            if (_cursor.isNull(_cursorIndexOfDoctorSpecialty)) {
              _tmpDoctorSpecialty = null;
            } else {
              _tmpDoctorSpecialty = _cursor.getString(_cursorIndexOfDoctorSpecialty);
            }
            final String _tmpAppointmentDate;
            if (_cursor.isNull(_cursorIndexOfAppointmentDate)) {
              _tmpAppointmentDate = null;
            } else {
              _tmpAppointmentDate = _cursor.getString(_cursorIndexOfAppointmentDate);
            }
            final String _tmpAppointmentTime;
            if (_cursor.isNull(_cursorIndexOfAppointmentTime)) {
              _tmpAppointmentTime = null;
            } else {
              _tmpAppointmentTime = _cursor.getString(_cursorIndexOfAppointmentTime);
            }
            final String _tmpSymptoms;
            if (_cursor.isNull(_cursorIndexOfSymptoms)) {
              _tmpSymptoms = null;
            } else {
              _tmpSymptoms = _cursor.getString(_cursorIndexOfSymptoms);
            }
            final double _tmpFeePaid;
            _tmpFeePaid = _cursor.getDouble(_cursorIndexOfFeePaid);
            final AppointmentStatus _tmpStatus;
            _tmpStatus = __AppointmentStatus_stringToEnum(_cursor.getString(_cursorIndexOfStatus));
            final String _tmpTransactionId;
            if (_cursor.isNull(_cursorIndexOfTransactionId)) {
              _tmpTransactionId = null;
            } else {
              _tmpTransactionId = _cursor.getString(_cursorIndexOfTransactionId);
            }
            final String _tmpPrescription;
            if (_cursor.isNull(_cursorIndexOfPrescription)) {
              _tmpPrescription = null;
            } else {
              _tmpPrescription = _cursor.getString(_cursorIndexOfPrescription);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new AppointmentEntity(_tmpId,_tmpPatientId,_tmpPatientName,_tmpDoctorId,_tmpDoctorName,_tmpDoctorSpecialty,_tmpAppointmentDate,_tmpAppointmentTime,_tmpSymptoms,_tmpFeePaid,_tmpStatus,_tmpTransactionId,_tmpPrescription,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __AppointmentStatus_enumToString(@NonNull final AppointmentStatus _value) {
    switch (_value) {
      case UPCOMING: return "UPCOMING";
      case IN_PROGRESS: return "IN_PROGRESS";
      case COMPLETED: return "COMPLETED";
      case CANCELLED: return "CANCELLED";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private AppointmentStatus __AppointmentStatus_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "UPCOMING": return AppointmentStatus.UPCOMING;
      case "IN_PROGRESS": return AppointmentStatus.IN_PROGRESS;
      case "COMPLETED": return AppointmentStatus.COMPLETED;
      case "CANCELLED": return AppointmentStatus.CANCELLED;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
