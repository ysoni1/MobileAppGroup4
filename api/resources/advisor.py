import datetime as dt

from flask_restplus import Resource, reqparse
from flask_jwt import jwt_required
from models.advisor import AdvisorModel

class AdvisorAPI(Resource):
    def post(self):
        parser = reqparse.RequestParser()    

        parser.add_argument('description', type=str, required=True, help="Field description cannot be empty")
        parser.add_argument('advice_date', type=lambda x: dt.datetime.strptime(x, "%Y-%m-%d").date(), required=True, help="Field advice_date cannot be empty")                
        parser.add_argument('owner_id', type=int, required=True, help="Field owner_id cannot be empty")                        

        payload = parser.parse_args()

        advice = AdvisorModel(payload['description'], payload['advice_date'], False, payload['owner_id'])
        advice.save()

        return {'message' : 'Advice created without issues'}, 201

class Advisor(Resource):
    def get(self, _id, owner):
        advice = AdvisorModel.find_by_id(_id, owner)

        if advice:
            return advice.json()
        else:
            return { 'message' : f'Advice {_id} not found - owner : {owner}'}, 404

    def delete(self, _id, owner):
        advice = AdvisorModel.find_by_id(_id, owner)

        if advice:
            advice.delete()

            return { 'message' : f'Advice {_id} deleted - owner : {owner}'}
        else:
            return { 'message' : f'Advice {_id} not found - owner : {owner}'}, 404
    
    def put(self, _id, owner):
        parser = reqparse.RequestParser()

        parser.add_argument('description', type=str, required=False, help="Field description cannot be empty")
        parser.add_argument('advice_date', type=lambda x: dt.datetime.strptime(x, "%Y-%m-%d").date(), required=False, help="Field advice_date cannot be empty")                
        parser.add_argument('complete', type=bool, required=False, help="Field complete cannot be empty")

        advice = AdvisorModel.find_by_id(_id, owner)

        if advice:
            payload = parser.parse_args()

            
            if payload['description']: advice.description = payload['description']
            if payload['advice_date']: advice.advice_date = payload['advice_date']
            if payload['complete']: advice.complete = payload['complete']

            advice.save()

            return { 'message' : f'Advice {_id} changed.'}, 201

        else:
            return { 'message' : f'Advice {_id} not found - owner : {owner}'}, 404


class AdviceList(Resource):
    def get(self, owner):
        return { 'advices' : [advice.json() for advice in AdvisorModel.find_by_owner(owner)]}








 




