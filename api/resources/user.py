from flask_restplus import Resource, Api, reqparse
from flask_jwt import jwt_required
from models.user import UserModel


class User(Resource):
    def post(self):
        parser = reqparse.RequestParser()
        
        parser.add_argument('username', type=str, required=True, help="Field username cannot be empty")
        parser.add_argument('password', type=str, required=True, help="Field password cannot be empty")
        parser.add_argument('first_name', type=str, required=True, help="Field first_name cannot be empty")
        parser.add_argument('last_name', type=str, required=True, help="Field last_name cannot be empty")
        parser.add_argument('email', type=str, required=True, help="Field email cannot be empty")
        
        payload = parser.parse_args()

        if UserModel.find_by_username(payload['username']):
            return {'message' : f'User {payload["username"]} already exits'}, 201

        else:
            user = UserModel(payload['username'], payload['password'], payload['first_name'], payload['last_name'], payload['email'] )
            user.save()
            
            return {'message' : 'User created without issues'}, 201
    
    def get(self, username):
        user = UserModel.find_by_username(username)

        if user:
            return user.json()
        else:
            return {'message' : f'User {username} not found'}, 404

    def delete(self, username):
        user = UserModel.find_by_username(username)

        if user:
            user.delete()
            
            return {'message' : f'User {username} deleted.'}

        else:
            return {'message' : f'User {username} not found'}, 404

    def put(self, username):
        parser = reqparse.RequestParser()
        parser.add_argument('password', type=str, required=True, help="Field password cannot be empty")        

        user = UserModel.find_by_username(username)

        if user:
            payload = parser.parse_args()

            user.password = payload['password']
            user.save()

            return {'message' : 'User password changed without issues'}, 201            
        else:
            return {'message' : f'User {username} not found'}, 404


class UserList(Resource):
    @jwt_required()
    def get(self):
        return { 'users' : [user.json() for user in UserModel.query.all()]}
