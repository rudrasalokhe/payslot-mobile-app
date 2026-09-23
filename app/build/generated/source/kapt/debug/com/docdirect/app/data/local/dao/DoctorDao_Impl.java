package com.docdirect.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.docdirect.app.data.local.entity.DoctorEntity;
import com.docdirect.app.data.local.entity.SlotEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class DoctorDao_Impl implements DoctorDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DoctorEntity> __insertionAdapterOfDoctorEntity;

  private final EntityInsertionAdapter<SlotEntity> __insertionAdapterOfSlotEntity;

  private final EntityDeletionOrUpdateAdapter<DoctorEntity> __updateAdapterOfDoctorEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteSlot;

  private final SharedSQLiteStatement __preparedStmtOfMarkSlotBooked;

  public DoctorDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDoctorEntity = new EntityInsertionAdapter<DoctorEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `doctors` (`id`,`userId`,`name`,`medicalLicense`,`specialty`,`qualification`,`experienceYears`,`consultationFee`,`bio`,`rating`,`reviewCount`,`isAvailable`,`hospitalAffiliation`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DoctorEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getUserId());
        }
        if (entity.getName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getName());
        }
        if (entity.getMedicalLicense() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getMedicalLicense());
        }
        if (entity.getSpecialty() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getSpecialty());
        }
        if (entity.getQualification() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getQualification());
        }
        statement.bindLong(7, entity.getExperienceYears());
        statement.bindDouble(8, entity.getConsultationFee());
        if (entity.getBio() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getBio());
        }
        statement.bindDouble(10, entity.getRating());
        statement.bindLong(11, entity.getReviewCount());
        final int _tmp = entity.isAvailable() ? 1 : 0;
        statement.bindLong(12, _tmp);
        if (entity.getHospitalAffiliation() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getHospitalAffiliation());
        }
      }
    };
    this.__insertionAdapterOfSlotEntity = new EntityInsertionAdapter<SlotEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `slots` (`id`,`doctorId`,`date`,`time`,`isBooked`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SlotEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getDoctorId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getDoctorId());
        }
        if (entity.getDate() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getDate());
        }
        if (entity.getTime() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getTime());
        }
        final int _tmp = entity.isBooked() ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
    this.__updateAdapterOfDoctorEntity = new EntityDeletionOrUpdateAdapter<DoctorEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `doctors` SET `id` = ?,`userId` = ?,`name` = ?,`medicalLicense` = ?,`specialty` = ?,`qualification` = ?,`experienceYears` = ?,`consultationFee` = ?,`bio` = ?,`rating` = ?,`reviewCount` = ?,`isAvailable` = ?,`hospitalAffiliation` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DoctorEntity entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getId());
        }
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getUserId());
        }
        if (entity.getName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getName());
        }
        if (entity.getMedicalLicense() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getMedicalLicense());
        }
        if (entity.getSpecialty() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getSpecialty());
        }
        if (entity.getQualification() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getQualification());
        }
        statement.bindLong(7, entity.getExperienceYears());
        statement.bindDouble(8, entity.getConsultationFee());
        if (entity.getBio() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getBio());
        }
        statement.bindDouble(10, entity.getRating());
        statement.bindLong(11, entity.getReviewCount());
        final int _tmp = entity.isAvailable() ? 1 : 0;
        statement.bindLong(12, _tmp);
        if (entity.getHospitalAffiliation() == null) {
          statement.bindNull(13);
        } else {
          statement.bindString(13, entity.getHospitalAffiliation());
        }
        if (entity.getId() == null) {
          statement.bindNull(14);
        } else {
          statement.bindString(14, entity.getId());
        }
      }
    };
    this.__preparedStmtOfDeleteSlot = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM slots WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkSlotBooked = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE slots SET isBooked = 1 WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertDoctor(final DoctorEntity doctor,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDoctorEntity.insert(doctor);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertSlot(final SlotEntity slot, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSlotEntity.insert(slot);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateDoctor(final DoctorEntity doctor,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfDoctorEntity.handle(doctor);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteSlot(final String slotId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteSlot.acquire();
        int _argIndex = 1;
        if (slotId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, slotId);
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
          __preparedStmtOfDeleteSlot.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markSlotBooked(final String slotId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkSlotBooked.acquire();
        int _argIndex = 1;
        if (slotId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, slotId);
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
          __preparedStmtOfMarkSlotBooked.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DoctorEntity>> getAllDoctors() {
    final String _sql = "SELECT * FROM doctors";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"doctors"}, new Callable<List<DoctorEntity>>() {
      @Override
      @NonNull
      public List<DoctorEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMedicalLicense = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalLicense");
          final int _cursorIndexOfSpecialty = CursorUtil.getColumnIndexOrThrow(_cursor, "specialty");
          final int _cursorIndexOfQualification = CursorUtil.getColumnIndexOrThrow(_cursor, "qualification");
          final int _cursorIndexOfExperienceYears = CursorUtil.getColumnIndexOrThrow(_cursor, "experienceYears");
          final int _cursorIndexOfConsultationFee = CursorUtil.getColumnIndexOrThrow(_cursor, "consultationFee");
          final int _cursorIndexOfBio = CursorUtil.getColumnIndexOrThrow(_cursor, "bio");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfIsAvailable = CursorUtil.getColumnIndexOrThrow(_cursor, "isAvailable");
          final int _cursorIndexOfHospitalAffiliation = CursorUtil.getColumnIndexOrThrow(_cursor, "hospitalAffiliation");
          final List<DoctorEntity> _result = new ArrayList<DoctorEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DoctorEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpMedicalLicense;
            if (_cursor.isNull(_cursorIndexOfMedicalLicense)) {
              _tmpMedicalLicense = null;
            } else {
              _tmpMedicalLicense = _cursor.getString(_cursorIndexOfMedicalLicense);
            }
            final String _tmpSpecialty;
            if (_cursor.isNull(_cursorIndexOfSpecialty)) {
              _tmpSpecialty = null;
            } else {
              _tmpSpecialty = _cursor.getString(_cursorIndexOfSpecialty);
            }
            final String _tmpQualification;
            if (_cursor.isNull(_cursorIndexOfQualification)) {
              _tmpQualification = null;
            } else {
              _tmpQualification = _cursor.getString(_cursorIndexOfQualification);
            }
            final int _tmpExperienceYears;
            _tmpExperienceYears = _cursor.getInt(_cursorIndexOfExperienceYears);
            final double _tmpConsultationFee;
            _tmpConsultationFee = _cursor.getDouble(_cursorIndexOfConsultationFee);
            final String _tmpBio;
            if (_cursor.isNull(_cursorIndexOfBio)) {
              _tmpBio = null;
            } else {
              _tmpBio = _cursor.getString(_cursorIndexOfBio);
            }
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final boolean _tmpIsAvailable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAvailable);
            _tmpIsAvailable = _tmp != 0;
            final String _tmpHospitalAffiliation;
            if (_cursor.isNull(_cursorIndexOfHospitalAffiliation)) {
              _tmpHospitalAffiliation = null;
            } else {
              _tmpHospitalAffiliation = _cursor.getString(_cursorIndexOfHospitalAffiliation);
            }
            _item = new DoctorEntity(_tmpId,_tmpUserId,_tmpName,_tmpMedicalLicense,_tmpSpecialty,_tmpQualification,_tmpExperienceYears,_tmpConsultationFee,_tmpBio,_tmpRating,_tmpReviewCount,_tmpIsAvailable,_tmpHospitalAffiliation);
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
  public Flow<DoctorEntity> getDoctorByIdFlow(final String doctorId) {
    final String _sql = "SELECT * FROM doctors WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (doctorId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, doctorId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"doctors"}, new Callable<DoctorEntity>() {
      @Override
      @Nullable
      public DoctorEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMedicalLicense = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalLicense");
          final int _cursorIndexOfSpecialty = CursorUtil.getColumnIndexOrThrow(_cursor, "specialty");
          final int _cursorIndexOfQualification = CursorUtil.getColumnIndexOrThrow(_cursor, "qualification");
          final int _cursorIndexOfExperienceYears = CursorUtil.getColumnIndexOrThrow(_cursor, "experienceYears");
          final int _cursorIndexOfConsultationFee = CursorUtil.getColumnIndexOrThrow(_cursor, "consultationFee");
          final int _cursorIndexOfBio = CursorUtil.getColumnIndexOrThrow(_cursor, "bio");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfIsAvailable = CursorUtil.getColumnIndexOrThrow(_cursor, "isAvailable");
          final int _cursorIndexOfHospitalAffiliation = CursorUtil.getColumnIndexOrThrow(_cursor, "hospitalAffiliation");
          final DoctorEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpMedicalLicense;
            if (_cursor.isNull(_cursorIndexOfMedicalLicense)) {
              _tmpMedicalLicense = null;
            } else {
              _tmpMedicalLicense = _cursor.getString(_cursorIndexOfMedicalLicense);
            }
            final String _tmpSpecialty;
            if (_cursor.isNull(_cursorIndexOfSpecialty)) {
              _tmpSpecialty = null;
            } else {
              _tmpSpecialty = _cursor.getString(_cursorIndexOfSpecialty);
            }
            final String _tmpQualification;
            if (_cursor.isNull(_cursorIndexOfQualification)) {
              _tmpQualification = null;
            } else {
              _tmpQualification = _cursor.getString(_cursorIndexOfQualification);
            }
            final int _tmpExperienceYears;
            _tmpExperienceYears = _cursor.getInt(_cursorIndexOfExperienceYears);
            final double _tmpConsultationFee;
            _tmpConsultationFee = _cursor.getDouble(_cursorIndexOfConsultationFee);
            final String _tmpBio;
            if (_cursor.isNull(_cursorIndexOfBio)) {
              _tmpBio = null;
            } else {
              _tmpBio = _cursor.getString(_cursorIndexOfBio);
            }
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final boolean _tmpIsAvailable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAvailable);
            _tmpIsAvailable = _tmp != 0;
            final String _tmpHospitalAffiliation;
            if (_cursor.isNull(_cursorIndexOfHospitalAffiliation)) {
              _tmpHospitalAffiliation = null;
            } else {
              _tmpHospitalAffiliation = _cursor.getString(_cursorIndexOfHospitalAffiliation);
            }
            _result = new DoctorEntity(_tmpId,_tmpUserId,_tmpName,_tmpMedicalLicense,_tmpSpecialty,_tmpQualification,_tmpExperienceYears,_tmpConsultationFee,_tmpBio,_tmpRating,_tmpReviewCount,_tmpIsAvailable,_tmpHospitalAffiliation);
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

  @Override
  public Flow<DoctorEntity> getDoctorByUserIdFlow(final String userId) {
    final String _sql = "SELECT * FROM doctors WHERE userId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"doctors"}, new Callable<DoctorEntity>() {
      @Override
      @Nullable
      public DoctorEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMedicalLicense = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalLicense");
          final int _cursorIndexOfSpecialty = CursorUtil.getColumnIndexOrThrow(_cursor, "specialty");
          final int _cursorIndexOfQualification = CursorUtil.getColumnIndexOrThrow(_cursor, "qualification");
          final int _cursorIndexOfExperienceYears = CursorUtil.getColumnIndexOrThrow(_cursor, "experienceYears");
          final int _cursorIndexOfConsultationFee = CursorUtil.getColumnIndexOrThrow(_cursor, "consultationFee");
          final int _cursorIndexOfBio = CursorUtil.getColumnIndexOrThrow(_cursor, "bio");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfIsAvailable = CursorUtil.getColumnIndexOrThrow(_cursor, "isAvailable");
          final int _cursorIndexOfHospitalAffiliation = CursorUtil.getColumnIndexOrThrow(_cursor, "hospitalAffiliation");
          final DoctorEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpMedicalLicense;
            if (_cursor.isNull(_cursorIndexOfMedicalLicense)) {
              _tmpMedicalLicense = null;
            } else {
              _tmpMedicalLicense = _cursor.getString(_cursorIndexOfMedicalLicense);
            }
            final String _tmpSpecialty;
            if (_cursor.isNull(_cursorIndexOfSpecialty)) {
              _tmpSpecialty = null;
            } else {
              _tmpSpecialty = _cursor.getString(_cursorIndexOfSpecialty);
            }
            final String _tmpQualification;
            if (_cursor.isNull(_cursorIndexOfQualification)) {
              _tmpQualification = null;
            } else {
              _tmpQualification = _cursor.getString(_cursorIndexOfQualification);
            }
            final int _tmpExperienceYears;
            _tmpExperienceYears = _cursor.getInt(_cursorIndexOfExperienceYears);
            final double _tmpConsultationFee;
            _tmpConsultationFee = _cursor.getDouble(_cursorIndexOfConsultationFee);
            final String _tmpBio;
            if (_cursor.isNull(_cursorIndexOfBio)) {
              _tmpBio = null;
            } else {
              _tmpBio = _cursor.getString(_cursorIndexOfBio);
            }
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final boolean _tmpIsAvailable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAvailable);
            _tmpIsAvailable = _tmp != 0;
            final String _tmpHospitalAffiliation;
            if (_cursor.isNull(_cursorIndexOfHospitalAffiliation)) {
              _tmpHospitalAffiliation = null;
            } else {
              _tmpHospitalAffiliation = _cursor.getString(_cursorIndexOfHospitalAffiliation);
            }
            _result = new DoctorEntity(_tmpId,_tmpUserId,_tmpName,_tmpMedicalLicense,_tmpSpecialty,_tmpQualification,_tmpExperienceYears,_tmpConsultationFee,_tmpBio,_tmpRating,_tmpReviewCount,_tmpIsAvailable,_tmpHospitalAffiliation);
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

  @Override
  public Object getDoctorByUserId(final String userId,
      final Continuation<? super DoctorEntity> $completion) {
    final String _sql = "SELECT * FROM doctors WHERE userId = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (userId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, userId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<DoctorEntity>() {
      @Override
      @Nullable
      public DoctorEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfMedicalLicense = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalLicense");
          final int _cursorIndexOfSpecialty = CursorUtil.getColumnIndexOrThrow(_cursor, "specialty");
          final int _cursorIndexOfQualification = CursorUtil.getColumnIndexOrThrow(_cursor, "qualification");
          final int _cursorIndexOfExperienceYears = CursorUtil.getColumnIndexOrThrow(_cursor, "experienceYears");
          final int _cursorIndexOfConsultationFee = CursorUtil.getColumnIndexOrThrow(_cursor, "consultationFee");
          final int _cursorIndexOfBio = CursorUtil.getColumnIndexOrThrow(_cursor, "bio");
          final int _cursorIndexOfRating = CursorUtil.getColumnIndexOrThrow(_cursor, "rating");
          final int _cursorIndexOfReviewCount = CursorUtil.getColumnIndexOrThrow(_cursor, "reviewCount");
          final int _cursorIndexOfIsAvailable = CursorUtil.getColumnIndexOrThrow(_cursor, "isAvailable");
          final int _cursorIndexOfHospitalAffiliation = CursorUtil.getColumnIndexOrThrow(_cursor, "hospitalAffiliation");
          final DoctorEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpUserId;
            if (_cursor.isNull(_cursorIndexOfUserId)) {
              _tmpUserId = null;
            } else {
              _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            }
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final String _tmpMedicalLicense;
            if (_cursor.isNull(_cursorIndexOfMedicalLicense)) {
              _tmpMedicalLicense = null;
            } else {
              _tmpMedicalLicense = _cursor.getString(_cursorIndexOfMedicalLicense);
            }
            final String _tmpSpecialty;
            if (_cursor.isNull(_cursorIndexOfSpecialty)) {
              _tmpSpecialty = null;
            } else {
              _tmpSpecialty = _cursor.getString(_cursorIndexOfSpecialty);
            }
            final String _tmpQualification;
            if (_cursor.isNull(_cursorIndexOfQualification)) {
              _tmpQualification = null;
            } else {
              _tmpQualification = _cursor.getString(_cursorIndexOfQualification);
            }
            final int _tmpExperienceYears;
            _tmpExperienceYears = _cursor.getInt(_cursorIndexOfExperienceYears);
            final double _tmpConsultationFee;
            _tmpConsultationFee = _cursor.getDouble(_cursorIndexOfConsultationFee);
            final String _tmpBio;
            if (_cursor.isNull(_cursorIndexOfBio)) {
              _tmpBio = null;
            } else {
              _tmpBio = _cursor.getString(_cursorIndexOfBio);
            }
            final double _tmpRating;
            _tmpRating = _cursor.getDouble(_cursorIndexOfRating);
            final int _tmpReviewCount;
            _tmpReviewCount = _cursor.getInt(_cursorIndexOfReviewCount);
            final boolean _tmpIsAvailable;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsAvailable);
            _tmpIsAvailable = _tmp != 0;
            final String _tmpHospitalAffiliation;
            if (_cursor.isNull(_cursorIndexOfHospitalAffiliation)) {
              _tmpHospitalAffiliation = null;
            } else {
              _tmpHospitalAffiliation = _cursor.getString(_cursorIndexOfHospitalAffiliation);
            }
            _result = new DoctorEntity(_tmpId,_tmpUserId,_tmpName,_tmpMedicalLicense,_tmpSpecialty,_tmpQualification,_tmpExperienceYears,_tmpConsultationFee,_tmpBio,_tmpRating,_tmpReviewCount,_tmpIsAvailable,_tmpHospitalAffiliation);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<SlotEntity>> getSlotsForDoctor(final String doctorId) {
    final String _sql = "SELECT * FROM slots WHERE doctorId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (doctorId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, doctorId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"slots"}, new Callable<List<SlotEntity>>() {
      @Override
      @NonNull
      public List<SlotEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDoctorId = CursorUtil.getColumnIndexOrThrow(_cursor, "doctorId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfTime = CursorUtil.getColumnIndexOrThrow(_cursor, "time");
          final int _cursorIndexOfIsBooked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBooked");
          final List<SlotEntity> _result = new ArrayList<SlotEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final SlotEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpDoctorId;
            if (_cursor.isNull(_cursorIndexOfDoctorId)) {
              _tmpDoctorId = null;
            } else {
              _tmpDoctorId = _cursor.getString(_cursorIndexOfDoctorId);
            }
            final String _tmpDate;
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _tmpDate = null;
            } else {
              _tmpDate = _cursor.getString(_cursorIndexOfDate);
            }
            final String _tmpTime;
            if (_cursor.isNull(_cursorIndexOfTime)) {
              _tmpTime = null;
            } else {
              _tmpTime = _cursor.getString(_cursorIndexOfTime);
            }
            final boolean _tmpIsBooked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBooked);
            _tmpIsBooked = _tmp != 0;
            _item = new SlotEntity(_tmpId,_tmpDoctorId,_tmpDate,_tmpTime,_tmpIsBooked);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
