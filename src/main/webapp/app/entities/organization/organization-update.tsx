import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IOrganization } from 'app/shared/model/organization.model';
import { AuthStateEnum } from 'app/shared/model/enumerations/auth-state-enum.model';
import { CountryEnum } from 'app/shared/model/enumerations/country-enum.model';
import { getEntity, updateEntity, createEntity, reset } from './organization.reducer';

export const OrganizationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const organizationEntity = useAppSelector(state => state.organization.entity);
  const loading = useAppSelector(state => state.organization.loading);
  const updating = useAppSelector(state => state.organization.updating);
  const updateSuccess = useAppSelector(state => state.organization.updateSuccess);
  const authStateEnumValues = Object.keys(AuthStateEnum);
  const countryEnumValues = Object.keys(CountryEnum);

  const handleClose = () => {
    navigate('/organization');
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
      ...organizationEntity,
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
          country: 'ECUADOR',
          ...organizationEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="coreApp.organization.home.createOrEditLabel" data-cy="OrganizationCreateUpdateHeading">
            <Translate contentKey="coreApp.organization.home.createOrEditLabel">Create or edit a Organization</Translate>
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
                  id="organization-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('coreApp.organization.identification')}
                id="organization-identification"
                name="identification"
                data-cy="identification"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('coreApp.organization.name')}
                id="organization-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('coreApp.organization.state')}
                id="organization-state"
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
              <ValidatedField
                label={translate('coreApp.organization.country')}
                id="organization-country"
                name="country"
                data-cy="country"
                type="select"
              >
                {countryEnumValues.map(countryEnum => (
                  <option value={countryEnum} key={countryEnum}>
                    {translate('coreApp.CountryEnum.' + countryEnum)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('coreApp.organization.address')}
                id="organization-address"
                name="address"
                data-cy="address"
                type="text"
              />
              <ValidatedField
                label={translate('coreApp.organization.phone')}
                id="organization-phone"
                name="phone"
                data-cy="phone"
                type="text"
              />
              <ValidatedField
                label={translate('coreApp.organization.email')}
                id="organization-email"
                name="email"
                data-cy="email"
                type="text"
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/organization" replace color="info">
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

export default OrganizationUpdate;
