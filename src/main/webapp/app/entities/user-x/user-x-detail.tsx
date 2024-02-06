import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-x.reducer';

export const UserXDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const userXEntity = useAppSelector(state => state.userX.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userXDetailsHeading">
          <Translate contentKey="coreApp.userX.detail.title">UserX</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userXEntity.id}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="coreApp.userX.email">Email</Translate>
            </span>
          </dt>
          <dd>{userXEntity.email}</dd>
          <dt>
            <span id="password">
              <Translate contentKey="coreApp.userX.password">Password</Translate>
            </span>
          </dt>
          <dd>{userXEntity.password}</dd>
          <dt>
            <span id="facebookId">
              <Translate contentKey="coreApp.userX.facebookId">Facebook Id</Translate>
            </span>
          </dt>
          <dd>{userXEntity.facebookId}</dd>
          <dt>
            <span id="googleId">
              <Translate contentKey="coreApp.userX.googleId">Google Id</Translate>
            </span>
          </dt>
          <dd>{userXEntity.googleId}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="coreApp.userX.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{userXEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="coreApp.userX.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{userXEntity.lastName}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="coreApp.userX.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{userXEntity.phone}</dd>
          <dt>
            <span id="birth">
              <Translate contentKey="coreApp.userX.birth">Birth</Translate>
            </span>
          </dt>
          <dd>{userXEntity.birth ? <TextFormat value={userXEntity.birth} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="gender">
              <Translate contentKey="coreApp.userX.gender">Gender</Translate>
            </span>
          </dt>
          <dd>{userXEntity.gender}</dd>
          <dt>
            <span id="nationality">
              <Translate contentKey="coreApp.userX.nationality">Nationality</Translate>
            </span>
          </dt>
          <dd>{userXEntity.nationality}</dd>
          <dt>
            <span id="address">
              <Translate contentKey="coreApp.userX.address">Address</Translate>
            </span>
          </dt>
          <dd>{userXEntity.address}</dd>
          <dt>
            <span id="state">
              <Translate contentKey="coreApp.userX.state">State</Translate>
            </span>
          </dt>
          <dd>{userXEntity.state}</dd>
        </dl>
        <Button tag={Link} to="/user-x" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-x/${userXEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserXDetail;
