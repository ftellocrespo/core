import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUserX } from 'app/shared/model/user-x.model';
import { getEntities as getUserXes } from 'app/entities/user-x/user-x.reducer';
import { IErrorLog } from 'app/shared/model/error-log.model';
import { getEntity, updateEntity, createEntity, reset } from './error-log.reducer';

export const ErrorLogUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const userXES = useAppSelector(state => state.userX.entities);
  const errorLogEntity = useAppSelector(state => state.errorLog.entity);
  const loading = useAppSelector(state => state.errorLog.loading);
  const updating = useAppSelector(state => state.errorLog.updating);
  const updateSuccess = useAppSelector(state => state.errorLog.updateSuccess);

  const handleClose = () => {
    navigate('/error-log');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUserXes({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  // eslint-disable-next-line complexity
  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.line !== undefined && typeof values.line !== 'number') {
      values.line = Number(values.line);
    }

    const entity = {
      ...errorLogEntity,
      ...values,
      user: userXES.find(it => it.id.toString() === values.user.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...errorLogEntity,
          user: errorLogEntity?.user?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="coreApp.errorLog.home.createOrEditLabel" data-cy="ErrorLogCreateUpdateHeading">
            <Translate contentKey="coreApp.errorLog.home.createOrEditLabel">Create or edit a ErrorLog</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="error-log-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('coreApp.errorLog.className')}
                id="error-log-className"
                name="className"
                data-cy="className"
                type="text"
              />
              <ValidatedField label={translate('coreApp.errorLog.line')} id="error-log-line" name="line" data-cy="line" type="text" />
              <ValidatedField label={translate('coreApp.errorLog.error')} id="error-log-error" name="error" data-cy="error" type="text" />
              <ValidatedField
                label={translate('coreApp.errorLog.erroyType')}
                id="error-log-erroyType"
                name="erroyType"
                data-cy="erroyType"
                type="text"
              />
              <ValidatedField
                label={translate('coreApp.errorLog.timestamp')}
                id="error-log-timestamp"
                name="timestamp"
                data-cy="timestamp"
                type="date"
              />
              <ValidatedField id="error-log-user" name="user" data-cy="user" label={translate('coreApp.errorLog.user')} type="select">
                <option value="" key="0" />
                {userXES
                  ? userXES.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/error-log" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default ErrorLogUpdate;
