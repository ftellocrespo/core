import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './action-generic.reducer';

export const ActionGenericDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const actionGenericEntity = useAppSelector(state => state.actionGeneric.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="actionGenericDetailsHeading">
          <Translate contentKey="coreApp.actionGeneric.detail.title">ActionGeneric</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{actionGenericEntity.id}</dd>
          <dt>
            <span id="message">
              <Translate contentKey="coreApp.actionGeneric.message">Message</Translate>
            </span>
          </dt>
          <dd>{actionGenericEntity.message}</dd>
        </dl>
        <Button tag={Link} to="/action-generic" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/action-generic/${actionGenericEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ActionGenericDetail;
