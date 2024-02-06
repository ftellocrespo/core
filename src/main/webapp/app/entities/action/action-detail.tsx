import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './action.reducer';

export const ActionDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const actionEntity = useAppSelector(state => state.action.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="actionDetailsHeading">
          <Translate contentKey="coreApp.action.detail.title">Action</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{actionEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="coreApp.action.name">Name</Translate>
            </span>
          </dt>
          <dd>{actionEntity.name}</dd>
          <dt>
            <Translate contentKey="coreApp.action.activity">Activity</Translate>
          </dt>
          <dd>{actionEntity.activity ? actionEntity.activity.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/action" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/action/${actionEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ActionDetail;
