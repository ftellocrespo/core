import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUserX } from 'app/shared/model/user-x.model';
import { AuthStateEnum } from 'app/shared/model/enumerations/auth-state-enum.model';
import { getEntity, updateEntity, createEntity, reset } from './user-x.reducer';

export const UserXUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const userXEntity = useAppSelector(state => state.userX.entity);
  const loading = useAppSelector(state => state.userX.loading);
  const updating = useAppSelector(state => state.userX.updating);
  const updateSuccess = useAppSelector(state => state.userX.updateSuccess);
  const authStateEnumValues = Object.keys(AuthStateEnum);

  const handleClose = () => {
    navigate('/user-x');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
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

    const entity = {
      ...userXEntity,
      ...values,
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
          state: 'ACTIVE',
          ...userXEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="coreApp.userX.home.createOrEditLabel" data-cy="UserXCreateUpdateHeading">
            <Translate contentKey="coreApp.userX.home.createOrEditLabel">Create or edit a UserX</Translate>
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
                  id="user-x-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('coreApp.userX.email')}
                id="user-x-email"
                name="email"
                data-cy="email"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('coreApp.userX.password')}
                id="user-x-password"
                name="password"
                data-cy="password"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('coreApp.userX.facebookId')}
                id="user-x-facebookId"
                name="facebookId"
                data-cy="facebookId"
                type="text"
              />
              <ValidatedField
                label={translate('coreApp.userX.googleId')}
                id="user-x-googleId"
                name="googleId"
                data-cy="googleId"
                type="text"
              />
              <ValidatedField
                label={translate('coreApp.userX.firstName')}
                id="user-x-firstName"
                name="firstName"
                data-cy="firstName"
                type="text"
              />
              <ValidatedField
                label={translate('coreApp.userX.lastName')}
                id="user-x-lastName"
                name="lastName"
                data-cy="lastName"
                type="text"
              />
              <ValidatedField label={translate('coreApp.userX.phone')} id="user-x-phone" name="phone" data-cy="phone" type="text" />
              <ValidatedField label={translate('coreApp.userX.birth')} id="user-x-birth" name="birth" data-cy="birth" type="date" />
              <ValidatedField label={translate('coreApp.userX.gender')} id="user-x-gender" name="gender" data-cy="gender" type="text" />
              <ValidatedField
                label={translate('coreApp.userX.nationality')}
                id="user-x-nationality"
                name="nationality"
                data-cy="nationality"
                type="text"
              />
              <ValidatedField label={translate('coreApp.userX.address')} id="user-x-address" name="address" data-cy="address" type="text" />
              <ValidatedField label={translate('coreApp.userX.state')} id="user-x-state" name="state" data-cy="state" type="select">
                {authStateEnumValues.map(authStateEnum => (
                  <option value={authStateEnum} key={authStateEnum}>
                    {translate('coreApp.AuthStateEnum.' + authStateEnum)}
                  </option>
                ))}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/user-x" replace color="info">
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

export default UserXUpdate;
