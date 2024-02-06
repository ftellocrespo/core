import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText, UncontrolledTooltip } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IOrganizationUser } from 'app/shared/model/organization-user.model';
import { AuthStateEnum } from 'app/shared/model/enumerations/auth-state-enum.model';
import { RoleEnum } from 'app/shared/model/enumerations/role-enum.model';
import { getEntity, updateEntity, createEntity, reset } from './organization-user.reducer';

export const OrganizationUserUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const organizationUserEntity = useAppSelector(state => state.organizationUser.entity);
  const loading = useAppSelector(state => state.organizationUser.loading);
  const updating = useAppSelector(state => state.organizationUser.updating);
  const updateSuccess = useAppSelector(state => state.organizationUser.updateSuccess);
  const authStateEnumValues = Object.keys(AuthStateEnum);
  const roleEnumValues = Object.keys(RoleEnum);

  const handleClose = () => {
    navigate('/organization-user');
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
      ...organizationUserEntity,
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
          role: 'ADMIN',
          ...organizationUserEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="coreApp.organizationUser.home.createOrEditLabel" data-cy="OrganizationUserCreateUpdateHeading">
            <Translate contentKey="coreApp.organizationUser.home.createOrEditLabel">Create or edit a OrganizationUser</Translate>
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
                  id="organization-user-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('coreApp.organizationUser.state')}
                id="organization-user-state"
                name="state"
                data-cy="state"
                type="select"
              >
                {authStateEnumValues.map(authStateEnum => (
                  <option value={authStateEnum} key={authStateEnum}>
                    {translate('coreApp.AuthStateEnum.' + authStateEnum)}
                  </option>
                ))}
              </ValidatedField>
              <UncontrolledTooltip target="stateLabel">
                <Translate contentKey="coreApp.organizationUser.help.state" />
              </UncontrolledTooltip>
              <ValidatedField
                label={translate('coreApp.organizationUser.role')}
                id="organization-user-role"
                name="role"
                data-cy="role"
                type="select"
              >
                {roleEnumValues.map(roleEnum => (
                  <option value={roleEnum} key={roleEnum}>
                    {translate('coreApp.RoleEnum.' + roleEnum)}
                  </option>
                ))}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/organization-user" replace color="info">
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

export default OrganizationUserUpdate;
