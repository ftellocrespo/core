import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './error-log.reducer';

export const ErrorLogDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const errorLogEntity = useAppSelector(state => state.errorLog.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="errorLogDetailsHeading">
          <Translate contentKey="coreApp.errorLog.detail.title">ErrorLog</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{errorLogEntity.id}</dd>
          <dt>
            <span id="className">
              <Translate contentKey="coreApp.errorLog.className">Class Name</Translate>
            </span>
          </dt>
          <dd>{errorLogEntity.className}</dd>
          <dt>
            <span id="line">
              <Translate contentKey="coreApp.errorLog.line">Line</Translate>
            </span>
          </dt>
          <dd>{errorLogEntity.line}</dd>
          <dt>
            <span id="error">
              <Translate contentKey="coreApp.errorLog.error">Error</Translate>
            </span>
          </dt>
          <dd>{errorLogEntity.error}</dd>
          <dt>
            <span id="erroyType">
              <Translate contentKey="coreApp.errorLog.erroyType">Erroy Type</Translate>
            </span>
          </dt>
          <dd>{errorLogEntity.erroyType}</dd>
          <dt>
            <span id="timestamp">
              <Translate contentKey="coreApp.errorLog.timestamp">Timestamp</Translate>
            </span>
          </dt>
          <dd>
            {errorLogEntity.timestamp ? <TextFormat value={errorLogEntity.timestamp} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="coreApp.errorLog.user">User</Translate>
          </dt>
          <dd>{errorLogEntity.user ? errorLogEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/error-log" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/error-log/${errorLogEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ErrorLogDetail;
