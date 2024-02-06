import process from 'app/entities/process/process.reducer';
import activity from 'app/entities/activity/activity.reducer';
import action from 'app/entities/action/action.reducer';
import organization from 'app/entities/organization/organization.reducer';
import userX from 'app/entities/user-x/user-x.reducer';
import organizationUser from 'app/entities/organization-user/organization-user.reducer';
import errorLog from 'app/entities/error-log/error-log.reducer';
import actionGeneric from 'app/entities/action-generic/action-generic.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  process,
  activity,
  action,
  organization,
  userX,
  organizationUser,
  errorLog,
  actionGeneric,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
