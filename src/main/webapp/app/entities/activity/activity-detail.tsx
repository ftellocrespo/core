import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './activity.reducer';

export const ActivityDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const activityEntity = useAppSelector(state => state.activity.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="activityDetailsHeading">
          <Translate contentKey="coreApp.activity.detail.title">Activity</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{activityEntity.id}</dd>
          <dt>
            <span id="type">
              <Translate contentKey="coreApp.activity.type">Type</Translate>
            </span>
          </dt>
          <dd>{activityEntity.type}</dd>
          <dt>
            <Translate contentKey="coreApp.activity.process">Process</Translate>
          </dt>
          <dd>{activityEntity.process ? activityEntity.process.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/activity" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/activity/${activityEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ActivityDetail;
