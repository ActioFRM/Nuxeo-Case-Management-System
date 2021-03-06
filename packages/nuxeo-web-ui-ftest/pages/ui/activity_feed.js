import BasePage from '../base';

export default class ActivityFeed extends BasePage {
  getActivity(activity) {
    this.el.waitForExist('.value span');
    return this.el.$$('.value span').find((e) => e.getText() === activity);
  }
}
