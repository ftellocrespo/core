import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, UncontrolledTooltip, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './organization-user.reducer';

export const OrganizationUserDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const organizationUserEntity = useAppSelector(state => state.organizationUser.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="organizationUserDetailsHeading">
          <Translate contentKey="coreApp.organizationUser.detail.title">OrganizationUser</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{organizationUserEntity.id}</dd>
          <dt>
            <span id="state">
              <Translate contentKey="coreApp.organizationUser.state">State</Translate>
            </span>
            <UncontrolledTooltip target="state">
              <Translate contentKey="coreApp.organizationUser.help.state" />
            </UncontrolledTooltip>
          </dt>
          <dd>{organizationUserEntity.state}</dd>
          <dt>
            <span id="role">
              <Translate contentKey="coreApp.organizationUser.role">Role</Translate>
            </span>
          </dt>
          <dd>{organizationUserEntity.role}</dd>
        </dl>
        <Button tag={Link} to="/organization-user" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/organization-user/${organizationUserEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default OrganizationUserDetail;
